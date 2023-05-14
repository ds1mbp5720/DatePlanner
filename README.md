# DatePlanner
## 데이트 플래너 App  
> 1. 일자 별 일정 계획을 세워 메모할수있으며 kakao map을 통해 일정 별 위치를 볼수있습니다.   
> 2. 서울시 문화행사 정보를 제공하며 선택한 행사 장소 주변 상권 정보를 제공하며 계획에 추가할수있습니다.  
> 3. 선택한 문화행사 혹은 이동한 kakao map의 중심부터 일정 거리 안에 주변상권(음식점, 카페, 문화시설) 정보를 얻을수있습니다.  
> 4. 개발 플랫폼:  Android Studio Dolphin, API Level 33
------------
### 개발환경
+ Android Studio Dolphin  
+ minSdkVersion : 22  
+ targetSdkVersion : 33 
+ Language : Kotlin 
------------
### Skill & Library
+ Materail3 UI/UX 
+ Rx에 기반한 UI Event 처리 
+ Room DB 
+ OkHttp4/Retrofit 활용 Coroutine을 이용한 REST 처리 
+ Dagger-Hilt 를 이용한 DI 
+ Databinding, ViewBinding
+ EventBus
------------
### 사용 API
+ kakao Map, Naver Map(별도 함수화)
+ kakao POI REST API
+ 서울 문화행사 정보
+ 서울 문화행사 시설 정보
------------  
### 주요기능
+ 서울시 각종 문화행사 정보 제공
+ 선택한 행사지 중심으로 주변 poi정보 제공(식당, 카페, 놀거리)
+ 확인한 정보들로 데이트 시간계획 작성
------------
### 실행사진
1. 일정 리스트 

https://user-images.githubusercontent.com/37658906/208428798-56fc63fd-058a-4f10-901f-a37961ee8417.mp4 

 1.1 일정 지도  
 
https://user-images.githubusercontent.com/37658906/208428820-cd672f89-fe89-4cbf-b221-2da7b1a7e2ad.mp4 

2. 행사 정보  

https://user-images.githubusercontent.com/37658906/208428850-b20e1361-02af-4216-b6c0-410f38ad7373.mp4 

3. 주변 상권  

https://user-images.githubusercontent.com/37658906/208428755-ca631237-bb3c-4c2a-93da-2a2de517822f.mp4 




