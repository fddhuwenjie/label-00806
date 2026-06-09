package com.fooddelivery.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fooddelivery.common.BusinessException;
import com.fooddelivery.common.Constants;
import com.fooddelivery.dto.OrderSubmitDTO;
import com.fooddelivery.dto.PageQueryDTO;
import com.fooddelivery.entity.*;
import com.fooddelivery.mapper.OrderMapper;
import com.fooddelivery.service.*;
import com.fooddelivery.vo.CartVO;
import com.fooddelivery.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final CartService cartService;
    private final AddressService addressService;
    private final MerchantService merchantService;
    private final DishService dishService;
    private final OrderItemService orderItemService;

    @Override
    @Transactional
    public OrderVO submitOrder(Long userId, OrderSubmitDTO dto) {
        // 获取购物车
        List<CartVO> cartItems = cartService.listByUserIdAndMerchantId(userId, dto.getMerchantId());
        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车为空");
        }

        // 获取地址
        Address address = addressService.getById(dto.getAddressId());
        if (address == null) {
            throw new BusinessException("地址不存在");
        }

        // 获取商家
        Merchant merchant = merchantService.getById(dto.getMerchantId());
        if (merchant == null) {
            throw new BusinessException("商家不存在");
        }

        // 从数据库重新查询菜品价格，验证价格是否变动
        List<Long> dishIds = cartItems.stream().map(CartVO::getDishId).toList();
        List<Dish> dishes = dishService.listByIds(dishIds);
        if (dishes.size() != cartItems.size()) {
            throw new BusinessException("部分菜品不存在或已下架");
        }

        // 构建菜品ID到菜品对象的映射
        Map<Long, Dish> dishMap = dishes.stream()
                .collect(Collectors.toMap(Dish::getId, d -> d));

        // 验证价格并计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartVO item : cartItems) {
            Dish dish = dishMap.get(item.getDishId());
            if (dish == null) {
                throw new BusinessException("菜品不存在");
            }
            // 比对价格，不一致则抛出异常
            if (dish.getPrice().compareTo(item.getDishPrice()) != 0) {
                throw new BusinessException("菜品价格已变动，请刷新后重新下单");
            }
            totalAmount = totalAmount.add(dish.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // 检查起送价
        if (totalAmount.compareTo(merchant.getMinPrice()) < 0) {
            throw new BusinessException("未达到起送价");
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setMerchantId(dto.getMerchantId());
        order.setAddressId(dto.getAddressId());
        order.setAddressSnapshot(JSONUtil.toJsonStr(address));
        order.setTotalAmount(totalAmount);
        order.setDeliveryFee(merchant.getDeliveryFee());
        order.setActualAmount(totalAmount.add(merchant.getDeliveryFee()));
        order.setStatus(Constants.ORDER_PENDING_PAY);
        order.setRemark(dto.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        save(order);

        // 创建订单明细
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartVO item : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setDishId(item.getDishId());
            orderItem.setDishName(item.getDishName());
            orderItem.setDishImage(item.getDishImage());
            orderItem.setDishPrice(item.getDishPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItems.add(orderItem);

            // 使用原子更新增加菜品销量
            LambdaUpdateWrapper<Dish> dishUpdateWrapper = new LambdaUpdateWrapper<>();
            dishUpdateWrapper.eq(Dish::getId, item.getDishId())
                    .setSql("sales = sales + " + item.getQuantity());
            dishService.update(dishUpdateWrapper);
        }
        orderItemService.saveBatch(orderItems);

        // 清空购物车
        cartService.clearCart(userId, dto.getMerchantId());

        // 使用原子更新增加商家月销量
        LambdaUpdateWrapper<Merchant> merchantUpdateWrapper = new LambdaUpdateWrapper<>();
        merchantUpdateWrapper.eq(Merchant::getId, dto.getMerchantId())
                .setSql("monthly_sales = monthly_sales + 1");
        merchantService.update(merchantUpdateWrapper);

        log.info("订单提交成功: orderNo={}", order.getOrderNo());

        return getDetail(order.getId());
    }

    @Override
    public IPage<OrderVO> pageQuery(PageQueryDTO dto) {
        Page<Order> page = new Page<>(dto.getPage(), dto.getPageSize());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (dto.getMerchantId() != null) {
            wrapper.eq(Order::getMerchantId, dto.getMerchantId());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(Order::getStatus, dto.getStatus());
        }

        wrapper.orderByDesc(Order::getCreateTime);
        IPage<Order> orderPage = page(page, wrapper);

        return orderPage.convert(this::convertToVO);
    }

    @Override
    public IPage<OrderVO> pageQueryForUser(Long userId, Integer status, Integer page, Integer pageSize) {
        Page<Order> pageObj = new Page<>(page, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        // 过滤用户已删除的订单
        wrapper.eq(Order::getUserDeleted, 0);

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        wrapper.orderByDesc(Order::getCreateTime);
        IPage<Order> orderPage = page(pageObj, wrapper);

        return orderPage.convert(this::convertToVO);
    }

    @Override
    public OrderVO getDetail(Long id) {
        Order order = getById(id);
        if (order == null) {
            return null;
        }
        return convertToVO(order);
    }

    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setMerchantId(order.getMerchantId());
        vo.setAddressSnapshot(order.getAddressSnapshot());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setDeliveryFee(order.getDeliveryFee());
        vo.setActualAmount(order.getActualAmount());
        vo.setStatus(order.getStatus());
        vo.setStatusText(getStatusText(order.getStatus()));
        vo.setRemark(order.getRemark());
        vo.setCreateTime(order.getCreateTime());
        vo.setPayTime(order.getPayTime());
        vo.setDeliverTime(order.getDeliverTime());
        vo.setCompleteTime(order.getCompleteTime());

        Merchant merchant = merchantService.getById(order.getMerchantId());
        if (merchant != null) {
            vo.setMerchantName(merchant.getName());
            vo.setMerchantLogo(merchant.getLogo());
        }

        List<OrderItem> items = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, order.getId()));
        vo.setItems(items);

        return vo;
    }

    private String getStatusText(Integer status) {
        return switch (status) {
            case Constants.ORDER_PENDING_PAY -> "待支付";
            case Constants.ORDER_PENDING_ACCEPT -> "待接单";
            case Constants.ORDER_DELIVERING -> "配送中";
            case Constants.ORDER_COMPLETED -> "已完成";
            case Constants.ORDER_CANCELLED -> "已取消";
            default -> "未知";
        };
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Order order = getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        order.setStatus(status);
        if (status == Constants.ORDER_DELIVERING) {
            order.setDeliverTime(LocalDateTime.now());
        } else if (status == Constants.ORDER_COMPLETED) {
            order.setCompleteTime(LocalDateTime.now());
        }

        updateById(order);
        log.info("订单状态更新: id={}, status={}", id, status);
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long id) {
        Order order = getById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != Constants.ORDER_PENDING_PAY) {
            throw new BusinessException("当前状态不可取消");
        }

        // 查询订单明细
        List<OrderItem> orderItems = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, id));

        // 回滚菜品销量
        for (OrderItem item : orderItems) {
            LambdaUpdateWrapper<Dish> dishUpdateWrapper = new LambdaUpdateWrapper<>();
            dishUpdateWrapper.eq(Dish::getId, item.getDishId())
                    .setSql("sales = sales - " + item.getQuantity());
            dishService.update(dishUpdateWrapper);
        }

        // 回滚商家月销量
        LambdaUpdateWrapper<Merchant> merchantUpdateWrapper = new LambdaUpdateWrapper<>();
        merchantUpdateWrapper.eq(Merchant::getId, order.getMerchantId())
                .setSql("monthly_sales = monthly_sales - 1");
        merchantService.update(merchantUpdateWrapper);

        order.setStatus(Constants.ORDER_CANCELLED);
        updateById(order);
        log.info("订单取消: id={}", id);
    }

    @Override
    public void confirmOrder(Long userId, Long id) {
        Order order = getById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != Constants.ORDER_DELIVERING) {
            throw new BusinessException("当前状态不可确认收货");
        }

        order.setStatus(Constants.ORDER_COMPLETED);
        order.setCompleteTime(LocalDateTime.now());
        updateById(order);
        log.info("订单确认收货: id={}", id);
    }

    @Override
    public void payOrder(Long userId, Long id) {
        Order order = getById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != Constants.ORDER_PENDING_PAY) {
            throw new BusinessException("当前状态不可支付");
        }

        order.setStatus(Constants.ORDER_PENDING_ACCEPT);
        order.setPayTime(LocalDateTime.now());
        updateById(order);
        log.info("订单支付成功: id={}", id);
    }

    @Override
    public void deleteOrder(Long userId, Long id) {
        Order order = getById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }

        // 只有已完成或已取消的订单可以删除
        if (order.getStatus() != Constants.ORDER_COMPLETED && order.getStatus() != Constants.ORDER_CANCELLED) {
            throw new BusinessException("只能删除已完成或已取消的订单");
        }

        // 软删除：仅标记为用户侧已删除，后台仍可见
        order.setUserDeleted(1);
        updateById(order);
        log.info("订单用户侧删除: id={}", id);
    }

    @Override
    @Transactional
    public void autoCancelExpiredOrders() {
        // 查找15分钟前创建且未支付的订单
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(15);
        List<Order> expiredOrders = list(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, Constants.ORDER_PENDING_PAY)
                .lt(Order::getCreateTime, expireTime));

        for (Order order : expiredOrders) {
            // 查询订单明细
            List<OrderItem> orderItems = orderItemService.list(new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, order.getId()));

            // 回滚菜品销量
            for (OrderItem item : orderItems) {
                LambdaUpdateWrapper<Dish> dishUpdateWrapper = new LambdaUpdateWrapper<>();
                dishUpdateWrapper.eq(Dish::getId, item.getDishId())
                        .setSql("sales = sales - " + item.getQuantity());
                dishService.update(dishUpdateWrapper);
            }

            // 回滚商家月销量
            LambdaUpdateWrapper<Merchant> merchantUpdateWrapper = new LambdaUpdateWrapper<>();
            merchantUpdateWrapper.eq(Merchant::getId, order.getMerchantId())
                    .setSql("monthly_sales = monthly_sales - 1");
            merchantService.update(merchantUpdateWrapper);

            order.setStatus(Constants.ORDER_CANCELLED);
            updateById(order);
            log.info("订单超时自动取消: id={}, orderNo={}", order.getId(), order.getOrderNo());
        }

        if (!expiredOrders.isEmpty()) {
            log.info("自动取消超时订单数量: {}", expiredOrders.size());
        }
    }

    /**
     * 生成订单号：日期时间(14位) + 随机数(4位) = 18位
     * 格式：yyyyMMddHHmmss + 4位随机数
     * 示例：202603101230001234
     */
    private String generateOrderNo() {
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return dateTime + random;
    }
}
