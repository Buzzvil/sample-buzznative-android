# BuzzNativeSample

Sample Application for BuzzNative

1. BuzzNative 연동 가이드 문서
https://docs.google.com/document/d/1cvcZ6PL1NcG0a3ObQZVp8na5ORz_ZN_C3t0Rp44kUpE/edit

2. 예시 레이아웃은 Feed 타입과 Banner 타입으로, 이 외에도 자유롭게 레이아웃을 구성하여 사용할 수 있다.

3. NativeAdActivity 클래스의 PLACEMENT_ID는 담당 어카운트 매니저에게 요청하여 받은 값으로 사용해야 한다.

## FULLSCREEN 광고

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

## Manual Impression
기본적으로 광고 지면의 50% 이상이 노출되면 자동으로 impression 체크가 되는데, 이것이 잘 되지 않는 경우 수동으로 Impression을 설정할 수 있다.

```java
ad.impression();
```
