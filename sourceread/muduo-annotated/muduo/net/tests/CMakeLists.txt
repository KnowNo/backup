add_executable(echoserver_unittest EchoServer_unittest.cc)
target_link_libraries(echoserver_unittest muduo_net)

add_executable(echoclient_unittest EchoClient_unittest.cc)
target_link_libraries(echoclient_unittest muduo_net)

add_executable(eventloop_unittest EventLoop_unittest.cc)
target_link_libraries(eventloop_unittest muduo_net)

add_executable(eventloopthread_unittest EventLoopThread_unittest.cc)
target_link_libraries(eventloopthread_unittest muduo_net)

add_executable(eventloopthreadpool_unittest EventLoopThreadPool_unittest.cc)
target_link_libraries(eventloopthreadpool_unittest muduo_net)

if(BOOSTTEST_LIBRARY)
add_executable(buffer_unittest Buffer_unittest.cc)
target_link_libraries(buffer_unittest muduo_net boost_unit_test_framework)

add_executable(buffer_cpp11_unittest Buffer_unittest.cc)
target_link_libraries(buffer_cpp11_unittest muduo_net_cpp11 boost_unit_test_framework)
set_target_properties(buffer_cpp11_unittest PROPERTIES COMPILE_FLAGS "-std=c++0x")

add_executable(inetaddress_unittest InetAddress_unittest.cc)
target_link_libraries(inetaddress_unittest muduo_net boost_unit_test_framework)
endif()

add_executable(tcpclient_reg1 TcpClient_reg1.cc)
target_link_libraries(tcpclient_reg1 muduo_net)

add_executable(timerqueue_unittest TimerQueue_unittest.cc)
target_link_libraries(timerqueue_unittest muduo_net)

