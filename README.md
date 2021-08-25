# Life
This app helps user to manage time more efficiently. User adds tasks with duration, clicks to one which he is going to do and app starts to count how much time left to finish task. While user does his task he can close the app, block a screen and he will be notified when ever task time will be over. After that user can chose another task or take a rest.
# Screenshots

Start page             |  Add button was clicked |  First item was added |  All items were added
:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:
![](https://user-images.githubusercontent.com/52213479/130746736-2d6b9327-9764-4d2c-a52b-8d5d30a2c078.jpg)  |  ![](https://user-images.githubusercontent.com/52213479/130746860-b610a7af-9849-41dc-ab1f-ea61b7ac4b7a.jpg) |  ![](https://user-images.githubusercontent.com/52213479/130749330-c14bdc50-a89d-40c5-bd86-664fc1d8746e.jpg) |  ![](https://user-images.githubusercontent.com/52213479/130749574-03ad6caf-bc12-4e21-907c-809d82f70779.jpg)

First item is selected             |  App is closed |  Screen is blocked |  App is opened again
:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:
![](https://user-images.githubusercontent.com/52213479/130750089-00c70035-e5a2-4217-9a90-1affae0365e6.jpg)  |  ![](https://user-images.githubusercontent.com/52213479/130750356-e42d0c28-1fa9-4ac9-ad27-86c4dc098ada.jpg) |  ![](https://user-images.githubusercontent.com/52213479/130750798-2f15176d-bacf-4b75-a2c8-a6a52de3fb6b.jpg) |  ![](https://user-images.githubusercontent.com/52213479/130751006-9de8f079-9e77-49ad-b59d-a676cba3c767.jpg)
# Login behind the app

MainActivity is a launcher activity with RecyclerView in it. RecyclerView contains tasks. When some task is clicked MainActivity starts a TimerService via bindService() and startService() methods. So TimerService is both bound and start service, it is responsible for counting time which left to finish clicked task. When TimerService is running MainActivity gets updated information from it and updates RecyclerView's adapter every second. When MainActivity becomes invisible it unbinds from TimerService and stops updating RecyclerView's adapter but TimerService doesn't stop working and displays task's information in notification. If user opens app again MainActivity will update RecyclerView's adapter every second in case of TimerService is running, otherwise it just will show current state of RecyclerView. 











