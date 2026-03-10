const { getAddressList, saveAddress, updateAddress, deleteAddress } = require('../../api/address')

Page({
  data: {
    form: { id: null, contactName: '', contactPhone: '', province: '', city: '', district: '', detail: '', isDefault: 0 },
    existingAddresses: []
  },

  onLoad(options) {
    this.loadExistingAddresses()
    if (options.id) {
      this.loadAddress(options.id)
    }
  },

  async loadExistingAddresses() {
    try {
      const addresses = await getAddressList()
      this.setData({ existingAddresses: addresses || [] })
    } catch (e) {
      console.error(e)
    }
  },

  async loadAddress(id) {
    const addresses = await getAddressList()
    const address = addresses.find(a => a.id == id)
    if (address) {
      this.setData({ form: address })
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [`form.${field}`]: e.detail.value })
  },

  onSwitchChange(e) {
    this.setData({ 'form.isDefault': e.detail.value ? 1 : 0 })
  },

  async saveAddress() {
    const { form, existingAddresses } = this.data
    if (!form.contactName || !form.contactPhone || !form.detail) {
      wx.showToast({ title: '请填写完整信息', icon: 'none' })
      return
    }

    // 手机号格式校验
    if (!/^1[3-9]\d{9}$/.test(form.contactPhone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return
    }

    // 新增时检查是否重复
    if (!form.id) {
      const isDuplicate = existingAddresses.some(addr => 
        addr.contactName === form.contactName &&
        addr.contactPhone === form.contactPhone &&
        addr.detail === form.detail
      )
      if (isDuplicate) {
        wx.showToast({ title: '该地址已存在', icon: 'none' })
        return
      }
    }

    wx.showLoading({ title: '保存中' })
    try {
      if (form.id) {
        await updateAddress(form)
      } else {
        await saveAddress(form)
      }
      wx.hideLoading()
      wx.showToast({ title: '保存成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 1500)
    } catch (e) {
      console.error(e)
      wx.hideLoading()
    }
  },

  async deleteAddress() {
    wx.showModal({
      title: '提示',
      content: '确定删除该地址吗？',
      success: async (res) => {
        if (res.confirm) {
          await deleteAddress(this.data.form.id)
          wx.showToast({ title: '已删除', icon: 'success' })
          setTimeout(() => wx.navigateBack(), 1500)
        }
      }
    })
  }
})
