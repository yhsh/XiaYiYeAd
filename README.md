# XiaYiYeAd
## 说明：
### 添加任意布局到穿山甲广告页面的方法
- 说下实现思路:我们在Application中监听所有的activity，然后用stack添加每一个activity到里面，判断当前的activity(也就是stack最后一次添加的Activity就是当前的activity),拿到当前activity后通过当前activity拿到最外层的布局FrameLayout,拿到FrameLayout后就可以添加任意的布局了
- 穿山甲广告SDK的激励视频页面是不提供添加自定义布局的
