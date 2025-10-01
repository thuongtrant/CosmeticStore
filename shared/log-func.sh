#!/bin/bash


# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
ORANGE='\033[0;33m'
NC='\033[0m' # No Color


SUCCESS_SYM="[✓]"
WARNING_SYM="[⚠]"
ERROR_SYM="[✗]"
INFO_SYM="[ℹ]"
LOADING_SYM="[⏳]"

CUR_LINE=1

print_global() {
    local color=$1
    local msg=$2
    local option=$3

    local version
    case $option in
        --inline)
            CUR_LINE=$((CUR_LINE + 1))
            printf "${color} $msg${NC}"
            ;;
        --rewrite)
            printf "\r${color} $msg${NC}"
            ;;
        *)
            CUR_LINE=$((CUR_LINE + 1))
            printf "${color} $msg${NC}\n"
            ;;
    esac
    return 0
}

print_success() {
    local msg="$SUCCESS_SYM $1"
    local option=$3
    print_global "$GREEN" "$msg" "$option"
}

print_warning() {
    local msg="$WARNING_SYM $1"
    local option=$3
    print_global "$YELLOW" "$msg" "$option"
}

print_error() {
    local msg="$ERROR_SYM $1"
    local option=$3
    print_global "$RED" "$msg" "$option"
}

print_info() {
    local msg="$INFO_SYM $1"
    local option=$3
    print_global "$BLUE" "$msg" "$option"
}

print_heading() {
    local msg="$1"
    local option=$2
    print_global "$PURPLE" "\n$msg" "$option"
}

print_loading_timeout() {
    trap "exit" SIGTERM
    local msg="$1"
    local timeout="${2:-60}" # Thời gian chờ mặc định là 60 giây
    local count_time=$((timeout*1000)) # Chuyển đổi giây sang mili giây
    local spinners=(⠋ ⠙ ⠹ ⠸ ⠼ ⠴ ⠦ ⠧ ⠇ ⠏)

    tput civis
    # sleep 0.05
    while (( count_time > 0 )); do
        for loading in "${spinners[@]}"; do
            print_global "$GREEN" "[$loading] $msg..." "--rewrite"
            sleep 0.25
            printf "\r%*s\r" "$(tput cols)" ""  # Xoá dòng
            ((count_time-=2500))
        done
    done
    printf "\r%*s\r" "$(tput cols)" ""  # Xoá dòng
    tput cnorm
}

print_loading_callback() {
    local msg="$1"
    local callback="$2"
    local timeout="${3:-60}" # Thời gian chờ mặc định là 60 giây
    local LOADING_PID=0

    # Bắt đầu hàm in loading trong một tiến trình nền
    print_loading_timeout "$msg" $timeout &
    LOADING_PID=$!

    # Dừng hàm in loading ngay lập tức sau khi hàm callback hoàn thành
    $callback
    module_stop_loading $LOADING_PID 0
}

module_stop_loading() {
    local PID=$1
    local after="${2:-0}" # Thời gian chờ mặc định là 0 giây

    if [ $PID -ne 0 ]; then
        sleep $after
        kill $PID 2>/dev/null
        wait $PID 2>/dev/null
        printf "\r%*s\r" "$(tput cols)" ""  # Xoá dòng
        tput cnorm
    fi
}

module_clean_line() {
    printf "\r%*s\r" "$(tput cols)" ""  # Xoá dòng
}

fake_long_task() {
    flag=$1
    sleep 0.25
    printf "\n"
    print_info "Starting a fake long task... $flag"
    sleep 3
    printf "\n"
    print_success "Fake long task completed"
}

# print_loading_callback "Loading" "fake_long_task ahihi" 2
# print_info "$CUR_LINE"