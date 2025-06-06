# Airport
桃園機場航班 + 匯率換算  

機場即時桃園機場航班資訊  
規格: 暫缺  
API實作: https://www.kia.gov.tw/API/InstantSchedule.ashx?AirFlyLine=2&AirFlyIO=2  

匯率換算  
獲取API_KEY: https://freecurrencyapi.com/  
規格：https://freecurrencyapi.com/docs/latest  
幣種數據：https://freecurrencyapi.com/docs/currency-list  
API實作: https://api.freecurrencyapi.com/v1/latest  

技術/功能概要：
1. 架構：MVVM  
2. 語言：Kotlin  
3. UI架構：Xml、Jetpack compose  
4. 網絡請求：Retrofit2、OkHttp3、RxJava2  
5. 控件：  
   - BottomNavigationView、NavigationRailView  
   - RecyclerView（multitype）、SwipeRefreshLayout  
   - ViewPager2、TabLayout  
   - Card、LazyColumn、OutlinedTextField、AlertDialog、CircularProgressIndicator  
   - ...  
6. 功能：  
   - 航班  
     - 搜索框：根據航空公司IATA國際代碼，為空代表搜索全部  
     - 航班列表  
       - 起飛 & 抵達
       - 根據 airFlyStatus 的不同值進行高亮提醒
     - 下拉刷新  
     - 下滑加載：  
       - 正在加載中 & 已滑到最底
       - 為了模擬此效果，每次只加載10條數據  
     - 每10秒刷新一次
       - 列表自動滑動到最頂部
       - 會出現加載的圖標
   - 匯率  
     - 輸入框：輸入主幣別的換算金額  
     - 主幣別彈窗切換：默認使用美金  
     - 其他幣別彈窗切換：最多顯示6個  
     - 其他幣別換算列表：與輸入框的金額實時更新
   - 支持旋轉
   - 支持黑夜/白天模式

