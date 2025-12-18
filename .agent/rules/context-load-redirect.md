---
trigger: always_on
---

# Quy Tắc Dự Án & Ngữ Cảnh - Redirect

> **IMPORTANT**: Nội dung thực tế của project context và agent rules nằm ở file bên dưới.  
> File này chỉ là redirect để bypass gitignore restrictions.

**Đọc file chính tại**: [docs/agent/rules/context-load.md](/docs/agent/rules/context-load.md)

---

## Lý Do Sử Dụng Redirect

**Vấn đề**:
- Files trong `.agent/rules/` bị gitignore → không thể edit trực tiếp qua agent tools
- Encoding issues khi dùng PowerShell scripts để update
- Khó maintain và track changes

**Giải pháp**:
- File thực tế nằm trong `docs/agent/rules/` → có thể edit bình thường
- File này được track bởi Git → dễ review changes
- Agent sẽ tự động đọc file thực khi cần load context