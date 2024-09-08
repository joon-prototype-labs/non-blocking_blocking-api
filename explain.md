## Test 1
- spec
    - vuser: 100
    - duration: 30s
    - timeout: 8s
    - vuser's api request: every 0.5 seconds
    - query call delay time: 1s
- explain
    - 초당 200번의 요청, 총 60,000(200*30)의 요청 발생
    - DB 단에서 의도적으로 쿼리를 delay하면서 DB단 병목 발생시킴.
    - 프로젝트 별 성능 비교 
      - TODO 작성 예정: 요청 처리 시간, 성공률, 네트워크 접속 시간(blocking으로 인한 이벤트 루프 delay?, 원인 파악 필요) 
      - mvc-api(webmvc + controller + jpa)
      - corouter-api(webflux + coRouter + jpa)
      - webflux-blocking-api(webflux + reactive + jpa)
      - webflux-non-blocking-api(webflux + reactive + r2dbc)

## Test 2

TODO 작성 예정:    
    ~~Test 2는 DB 처리 시간을 짦게 가져가서 WAS 단 병목을 어느정도로 버틸 수 있는지 확인.~~   
    ~~뭐 근데 이미 Test 1에서 이미 한거 아닌가?`~~
    ~~Test 3에서 delay를 엄청 길게 잡아서 DB 단 부하가 심하면 성능이 비슷하다는걸 검증해봐야 하나?~~
    Test 1으로도 충분한 결과가 나온거 같은데? 분석 해보고 테스트 더 할지말지 결졍
