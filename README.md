# BuzzNativeSample - 전면 광고 버전

Sample Application for BuzzNative

이 문서는 전면 광고를 사용하는 경우에 특정하여 설명하기 위한 문서이다.
일반 적인 경우는 [원본 버전](https://github.com/Buzzvil/BuzzNativeSample/tree/master) 참고.

1. BuzzNative 연동 가이드 문서
https://docs.google.com/document/d/1cvcZ6PL1NcG0a3ObQZVp8na5ORz_ZN_C3t0Rp44kUpE/edit

2. 예시 레이아웃은 전면 광고 타입으로, 이 외에도 자유롭게 레이아웃을 구성하여 사용할 수 있다.

3. PLACEMENT_ID는 담당 어카운트 매니저에게 요청하여 받은 후, app/build.gradle 파일에서 `YOUR_APP_KEY` 부분을 수정하면 샘플 앱을 사용할 수 있다.


## FULLSCREEN 광고 (v2.0.6)
### FULLSCREEN 광고 받는 방법
NativeAd 객체에 다음과 같이 FULLSCREEN 광고를 받을 수 있도록 설정한다.

```java
import com.buzzvil.buzzad.AdType;
...

NativeAd nativeAd = new NativeAd(context, PLACEMENT_ID);
nativeAd.enableAdType(AdType.FULLSCREEN);
nativeAd.loadAd();
```

### 받은 광고가 FULLSCREEN인지 확인하는 방법
광고를 받으면 불리는 `AdListener.onAdLoaded(Ad ad)` 콜백에서 ad 객체의 광고 타입을 확인할 수 있다.

```java
AdListener nativeAdListener = new AdListener() {

    @Override
    public void onAdLoaded(Ad ad) {
        if (ad.getAdType() == AdType.FULLSCREEN) {
            // Do with fullscreen ad
        }
    }
}
```

## Manual Impression (v2.0.3)
기본적으로 광고 지면의 50% 이상이 노출되면 자동으로 impression 체크가 되는데, 이것이 잘 되지 않는 경우 수동으로 Impression을 설정할 수 있다.

```java
ad.impression();
```


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
