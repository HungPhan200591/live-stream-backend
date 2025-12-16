---
description: Kill process đang sử dụng port cụ thể trên Windows
---

# Workflow Kill Port

Workflow này sẽ kill process đang sử dụng một port cụ thể trên Windows.

**Cách dùng:** `/killport <số_port>`

**Ví dụ:** `/killport 8080`

// turbo-all

## Các bước:

1. Tìm Process ID (PID) đang sử dụng port:
```powershell
$port = "{{PORT}}"
$pid = (Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue).OwningProcess
if ($pid) {
    $processName = (Get-Process -Id $pid -ErrorAction SilentlyContinue).ProcessName
    Write-Host "Tìm thấy process đang dùng port $port : PID $pid ($processName)"
} else {
    Write-Host "Không tìm thấy process nào đang dùng port $port"
    exit 0
}
```

2. Kill process:
```powershell
if ($pid) {
    Stop-Process -Id $pid -Force
    Write-Host "✅ Đã kill thành công process $pid ($processName) trên port $port"
} else {
    Write-Host "Không có process nào để kill"
}
```

3. Kiểm tra port đã free chưa:
```powershell
Start-Sleep -Seconds 1
$check = (Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue)
if ($check) {
    Write-Host "⚠️ Cảnh báo: Port $port vẫn đang được sử dụng"
} else {
    Write-Host "✅ Port $port đã free"
}
```

## Lưu ý:

- Thay `{{PORT}}` bằng số port thực tế khi chạy
- Cần quyền Administrator trên Windows
- Lệnh này sẽ force kill process (terminate ngay lập tức)

## Lệnh nhanh (alternative):

Để kill port 8080 cụ thể:
```powershell
$pid = (Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue).OwningProcess; if ($pid) { Stop-Process -Id $pid -Force; Write-Host "Đã kill PID $pid" }
```
