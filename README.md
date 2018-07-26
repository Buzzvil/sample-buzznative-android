# Sample BuzzNative Android

Sample Application for BuzzNative Android SDK

1. BuzzNative 연동 가이드 문서
https://docs.google.com/document/d/1cvcZ6PL1NcG0a3ObQZVp8na5ORz_ZN_C3t0Rp44kUpE/edit

2. 예시 레이아웃은 다음 3개이며, 이 외에도 자유롭게 레이아웃을 구성하여 사용할 수 있다.
* Feed 스타일
* Banner 스타일
* RecyclerView 스타일

3. PLACEMENT_ID는 담당 어카운트 매니저에게 요청하여 받은 후, app/build.gradle 파일에서 `YOUR_APP_KEY` 부분을 수정하면 샘플 앱을 사용할 수 있다.


## Release Note

### v2.0.6
* impression 체크 로직의 버그 수정
* SponsoredText 문구를 퍼블리셔가 변경 가능
* AdType 추가

### v2.0.5
* 불필요한 위치 권한(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION) 삭제

### v2.0.4
* 제목 터치 가능하게 수정
* Ad와 NativeAdView간의 종속성 제거 (ViewHolder 사용 가능)

### v2.0.3
* 수동 impression 추가
