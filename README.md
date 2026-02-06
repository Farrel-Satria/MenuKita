# 🍽️ PROYEK: MenuKita
Aplikasi Manajemen Menu Restoran berbasis Android dengan **Firebase Realtime Database**.

---

## 🎨 TEMA: NAVY – CREAM
*   **Primary:** `#1A237E` (Navy)
*   **Secondary:** `#FFF8E1` (Cream)
*   **Accent:** `#FFC107` (Gold)
*   **Background:** Putih | **Text:** `#212121`

---

## 🚀 PANDUAN SETUP AWAL (WAJIB) ⚠️
Agar aplikasi bisa terhubung ke database, lakukan langkah ini setelah `git clone`:
1.  **Download Config:** Ambil file `google-services.json` dari Firebase Console (Project MenuKita).
2.  **Pindahkan File:** Simpan di folder `app/google-services.json`.
3.  **Sync Gradle:** Klik ikon gajah (**Sync Project with Gradle Files**) di Android Studio.

---

## 📅 PHASE PENGERJAAN (ROADMAP)

### 🏗️ PHASE 1: Foundation & Connectivity ✅
**Penanggung Jawab: Farrel**
*   [x] Setup Project & Konfigurasi Firebase.
*   [x] Aktivasi Realtime Database.
*   **Output:** Aplikasi terhubung ke Firebase.

### 🎨 PHASE 2: UI Base & Data Modeling ✅
**Penanggung Jawab: Dani & Musa**
*   [x] Implementasi warna & tema Navy-Cream.
*   [x] Finalisasi data class `Menu.kt`.
*   **Output:** Tampilan dasar dan model data fix.

### ⚙️ PHASE 3: Core CRUD Logic ✅
**Penanggung Jawab: Musa**
*   [x] Buat `MenuRepository.kt` (Create, Read, Update, Delete).
*   **Output:** Logika backend siap.

### 🔗 PHASE 4: UI Integration ✅
**Penanggung Jawab: Dani & Musa**
*   [x] Hubungkan Firebase ke `RecyclerView`.
*   [x] Buat halaman `AddMenuActivity`.
*   [x] Buat halaman `EditMenuActivity`.
*   **Output:** Fitur CRUD bisa digunakan sepenuhnya oleh user.

### 🛠️ PHASE 5: Testing & UX Polish 🚧
**Penanggung Jawab: Mazaya**
*   [x] Tambahkan Dialog Konfirmasi hapus data.
*   [ ] Uji coba input (Validasi kosong/format salah).
*   [ ] Handling error saat internet mati.
*   **Output:** Aplikasi stabil dan minim bug.

### 📝 PHASE 6: Documentation & Finalization
**Penanggung Jawab: Andhika**
*   [ ] Screenshot fitur & Pembuatan presentasi.
*   [ ] Finalisasi file `README.md`.
*   **Output:** Project siap dikumpulkan.

---

## 👥 TIM PENGEMBANG
*   **Farrel:** Cloud Architect & Firebase Setup
*   **Musa:** Logic Developer (Kotlin)
*   **Dani:** UI/UX Designer (XML)
*   **Mazaya:** Quality Assurance (Testing)
*   **Andhika:** Documentation Specialist
