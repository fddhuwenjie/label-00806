Component({
  properties: {
    // 是否显示骨架屏
    loading: {
      type: Boolean,
      value: true
    },
    // 骨架屏数量
    count: {
      type: Number,
      value: 3
    },
    // 是否显示动画
    animate: {
      type: Boolean,
      value: true
    }
  }
})
