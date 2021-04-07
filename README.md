### Image File의 특정 영역 문자열 추출

+ INTRO
  + Open Source Tess4j를 활용한 OCR library 
  + Image File의 특정 영역을 추출하여 추출한 영역의 문자열을 해독하는 기능

+ ROLE
  + Open Source Tess4j 분석
  + BufferedImage를 활용한 특정 이미지 영역 추출(x좌표, y좌표, width, height)
  + PNGDecoder로 RGB 추출 및 Image bytes 변환
  + Tess4j Add Refactoring 작업
  + Image File Text 추출 검증을 위한 Test Application 개발

+ GUIDE
  + 제공되는 [Test.java](https://github.com/suhojang/ImageTextExport/blob/main/src/Test.java)를 참조한다.

```
git clone https://github.com/suhojang/ImageTextExport.git
```
