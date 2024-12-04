# README.txt
# Group: ĐA19

## DỰ ÁN
Ứng dụng học từ vựng tiếng Anh

## MÔ TẢ
   Ứng dụng hỗ trợ người dùng lưu trữ các topic với các từ vựng tiếng Anh, cho phép học bằng flashcard, ôn tập bằng hình thức
   Trắc nghiệm hoặc Gõ từ, ngoài ra cũng hỗ trợ chia sẻ các topic giữa các người dùng với nhau.

## MỤC LỤC
1. Yêu cầu
2. Thông tin phần mềm
3. Các tài khoản có sẵn
4. Tập tin có sẵn
5. Clone dự án
6. Các lưu ý khác
7. Liên kết video Demo

1. Yêu cầu
   - Đảm bảo [Java SDK version] được cài đặt chính xác.
   - Lưu đường dẫn SDK location đúng với đường dẫn đã cài trên máy của bạn(nếu cần):
        Go to: File --> Project Structure --> SDK Location
   - Hãy kiểm tra các thông tin phần mềm trước khi chạy ứng dụng
   - Hãy đặt các file nhập, xuất mẫu vào các thư mục dễ nhận thấy và cho phép ứng dụng truy cập 'documents'.
   - Hãy chạy thử nghiệm ứng dụng ở chế độ DEBUG MODE.

2. Thông tin phần mềm:
   - compileSdk = 34
   - minSdk = 24
   - targetSdk = 34
   - gradle version = 8.9
   - gradle plugin version = 8.6.0
   - Các thư viện được sử dụng:
        + Đổ ảnh lên imageView: 
            implementation(libs.glide)
        + To Store images: 
            implementation(libs.cloudinary.android)
        + Import/Export csv files:
            implementation(libs.opencsv)


3. Các tài khoản có sẵn:
	Các tài khoản sẵn có:
		thongcv12vn@gmail.com - password: 123456
		bdtd1ad@gmail.com - password: 123456
        dogiahuy2252004@gmail.com - password: 123456
    Sử dụng email để đăng nhập, đăng ký tài khoản. Nhằm đảm bảo tính bảo mật tài khoản người dùng, xin hãy
    sử dụng email thật và không sử dụng email của người khác. Các chức năng Đổi mật khẩu, Quên mật khẩu đều sẽ cần
    truy xuất dữ liệu thông qua email.




4. Tập tin có sẵn:
    ! Xin hãy kiểm tra trong dự án gồm 1 file mẫu Nhập từ vựng vào topic và 1 tập tin .apk
    - File nhập từ vựng mẫu: word_list_ex_04_12_2024_142726.csv
    - Tập tin .apk: app.apk
    - Nên đặt file demo trong thư mục 'documents' trong Android
    - Tất cả các file xuất sẽ được lưu tại: storage/emulated/0/Download/EStudy/exported/

5. Clone dự án:
    Nếu dự án gặp sự cố hoặc thiếu tài nguyên, vui long clone lại dự án và lấy lại các tài nguyên cần thiết:
    - Choose branch `main`:
            git clone https://gitlab.duthu.net/S52200105/flashcard_groupa.git
    - Các tài nguyên liên quan cũng được pushs lên dự án repository đó.
    

6. Các lưu ý khác:
    - Sau khi đăng nhập, thông tin tài khoản được lưu trong SharedPreferences, do đó bạn sẽ không cần phải đăng nhập lại khi khởi động lại ứng dụng. 
    Tuy nhiên, bạn có thể đăng xuất bằng cách: Chọn Cá nhân --> ĐĂNG XUẤT --> Chọn YES để xác nhận. 
    - Trong quá trình chạy dự án, vui lòng cho phép tất cả các quyền cần thiết mà ứng dụng yêu cầu trên thiết bị của bạn. 
    - Test: Dự án đã được thử nghiệm trên Thiết bị ảo Android (AVD) với cấu hình: Medium Phone API 32 (Android 12L "Sv2", x86_64). 
    - Nếu bạn đang thử nghiệm trên thiết bị ghép nối, vui lòng chạy nó dưới chế độ DEBUG MODE.

7. Liên kết video Demo:
    Youtube: https://www.youtube.com/watch?v=hlWgUQCeroY
