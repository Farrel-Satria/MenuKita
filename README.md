# 🍽️ MenuKita Android App

Aplikasi Android untuk manajemen menu menggunakan **Firebase Realtime Database** sebagai backend. Project ini dikembangkan sebagai tugas akademik.

---

## 👥 Tim Pengembang
* **Frontend:** Dani  
* **Backend Firebase:** Farrel (Project Owner)  
* **Backend Kotlin:** Musa  
* **Debugging:** Mazaya  
* **Dokumentasi:** Andhika  

---

## 🚀 Panduan Setup Tim (WAJIB BACA!) ⚠️

Karena kita menggunakan satu database yang sama, file konfigurasi Firebase (`google-services.json`) **tidak dimasukkan ke GitHub** demi keamanan. Ikuti langkah ini setelah kamu melakukan `git clone`:
Sebelum menerima undangann kasi dulu email kalian ke aku
1. **Terima Undangan:** Pastikan kamu sudah menerima email undangan dari Farrel untuk bergabung ke project Firebase "MenuKita".
2. **Download Config:** - Masuk ke [Firebase Console](https://console.firebase.google.com/).
   - Pilih project **MenuKita**.
   - Klik ikon ⚙️ (**Project Settings**) > Tab **General**.
   - Scroll ke bawah ke bagian **Your Apps**, lalu klik tombol **google-services.json**.
3. **Pindahkan File:** Masukkan file tersebut ke folder project kamu di:  
   `app/google-services.json`
4. **Sync Gradle:** Buka Android Studio, klik ikon gajah (**Sync Project with Gradle Files**) di pojok kanan atas.

---

## 📂 Struktur Package Utama
```text
com.example.menukita
│
├── model          # Data class (Menu.kt)
├── ui             # Activity & Fragment (MainActivity.kt)
└── network/db     # Konfigurasi/Helper Firebase (Optional)
🛠️ Tech Stack
Language: Kotlin

IDE: Android Studio (Ladybug / Jellyfish)

Database: Firebase Realtime Database

Dependency Management: Version Catalog (libs.versions.toml)

✅ Status Fitur
[x] Setup Firebase & Version Catalog

[x] Model Data Menu

[ ] Create Menu (In Progress 🚧)

[ ] Read Menu List (Planned)

[ ] Update & Delete Menu (Planned)

📌 Catatan untuk Tim
Git Policy: Selalu buat branch baru jika ingin menambah fitur (git checkout -b fitur-nama).

Conflict: Lakukan git pull origin main secara rutin agar kode kita selalu sinkron.

Security: Jangan pernah menghapus google-services.json dari file .gitignore.
