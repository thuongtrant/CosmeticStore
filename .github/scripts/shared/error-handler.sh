#!/bin/bash

DIR_NAME="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ::::: Source :::::
source "$DIR_NAME/log-func.sh"

# :::: Functions ::::
function error() {
  	print_error "ERROR: ${BASH_SOURCE[1]} at line ${BASH_LINENO[0]}"
}

function throw_error() {
  	print_error "ERROR: ${BASH_SOURCE[1]} at line ${LINENO}"
	exit 1
}




# :::: Example ::::
set -o errtrace

# Siggle trap
trap throw_error ERR # ERR | EXIT | DEBUG | RETURN | 0 | SIGINT | SIGTERM | ...

# Multiple trap
trap 'throw_error; echo "Cleaning up..."; exit 1' ERR INT TERM # on interrupt (Ctrl + C), terminate (kill)

# :::: Run Example ::::
function simulate_single_trap() {
  	echo "Before fail"
  	false       # force an error
  	echo "After fail"   # will not run because trap will stop here due to "exit 1" at traperr function)
}

function simulate_multiple_trap() {
  	echo "Before Terminate"
  	sleep 5
  	# kill -s INT $$  # Uncomment to simulate interrupt (Ctrl + C)
  	# kill -s TERM $$ # Uncomment to simulate terminate (kill)
  	echo "After fail"   # will not run because trap will stop here due to "exit 1" at traperr function)
}

# simulate_multiple_trap
simulate_single_trap

# :::: Reference ::::

# BASH Cheetsheet: https://devhints.io/bash#miscellaneous
# BASH VARS: https://web.archive.org/web/20230318164746/https://wiki.bash-hackers.org/syntax/shellvars

# 1. Các loại trap phổ biến nhất

# | Tên trap (tín hiệu) | Ý nghĩa                                                   | Khi nào phát sinh                                    | Thường dùng để làm gì                      |
# | ------------------- | --------------------------------------------------------- | ---------------------------------------------------- | ------------------------------------------ |
# | `EXIT`              | Khi script hoặc shell thoát (exit)                        | Bất cứ lúc nào script kết thúc                       | Dọn dẹp tài nguyên, log, clean up file tạm |
# | `ERR`               | Khi bất kỳ lệnh nào trả về lỗi (exit code ≠ 0)            | Khi có lỗi trong script                              | Log lỗi, debug, dừng script, gửi alert     |
# | `INT`               | Nhận Ctrl+C (SIGINT)                                      | Khi người dùng nhấn Ctrl+C                           | Dọn dẹp, dừng an toàn, báo user            |
# | `TERM`              | SIGTERM – tín hiệu kết thúc process từ ngoài              | Khi process bị kill hoặc systemd/cron gửi terminate  | Dọn dẹp trước khi bị kill, lưu trạng thái  |
# | `HUP`               | SIGHUP – treo kết nối terminal                            | Khi user đóng terminal SSH, hoặc process parent chết | Restart/reload config, log lại sự kiện     |
# | `SIGQUIT`           | Nhận Ctrl+\\                                              | Khi user nhấn Ctrl+\\                                | Debug hoặc dừng đặc biệt                   |
# | `SIGPIPE`           | Pipe bị ngắt (ví dụ: process đọc xong trước khi ghi xong) | Khi lệnh trong pipeline fail                         | Tránh lỗi pipe “Broken pipe”               |
# | `DEBUG`             | Trước khi chạy mỗi lệnh trong script                      | Lệnh nào cũng chạy trước khi thực thi                | Debug, in trace từng lệnh                  |
# | `RETURN`            | Khi function trả về                                       | Trong function                                       | Cleanup function                           |
# | `0`                 | Alias của EXIT                                            | Khi script kết thúc                                  | Cleanup                                    |


# 2. Các tín hiệu (signals) phổ biến trong Linux/Unix
# 
# | Tên tín hiệu | Số | Ý nghĩa                  |
# | ------------ | -- | ------------------------ |
# | SIGHUP       | 1  | Treo kết nối terminal    |
# | SIGINT       | 2  | Ctrl+C                   |
# | SIGQUIT      | 3  | Ctrl+\\                  |
# | SIGILL       | 4  | Illegal instruction      |
# | SIGABRT      | 6  | Abort                    |
# | SIGFPE       | 8  | Floating point exception |
# | SIGKILL      | 9  | Kill không thể trap      |
# | SIGSEGV      | 11 | Segmentation fault       |
# | SIGPIPE      | 13 | Pipe bị gãy              |
# | SIGALRM      | 14 | Alarm clock              |
# | SIGTERM      | 15 | Kết thúc process         |
# | SIGUSR1      | 10 | User-defined 1           |
# | SIGUSR2      | 12 | User-defined 2           |
# | SIGCHLD      | 17 | Child process dừng       |
# | SIGCONT      | 18 | Continue                 |
# | SIGSTOP      | 19 | Stop process             |
# | SIGTSTP      | 20 | Ctrl+Z (tạm dừng)        |

# Lưu ý: SIGKILL và SIGSTOP không thể trap (không shell nào bắt được).