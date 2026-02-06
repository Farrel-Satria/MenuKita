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

### 🏗️ PHASE 1: Foundation & Connectivity (Target: Firebase Konek) ✅
**Penanggung Jawab: Farrel**
*   [x] Setup Project di Firebase Console.
*   [x] Konfigurasi `google-services.json` ke Android Studio.
*   [x] Aktivasi Realtime Database (Rules: `read: true, write: true` untuk development).
*   [x] Uji coba push data dummy dari `MainActivity`.
*   **Output:** Aplikasi berhasil mengirim data ke Firebase.

### 🎨 PHASE 2: UI Base & Data Modeling (Target: Kerangka Siap) ✅
**Penanggung Jawab: Dani & Musa**
*   **Dani (UI):** Implementasi warna Navy-Cream di `colors.xml` & `themes.xml`. Buat layout dasar `activity_main.xml`.
*   [x] **Musa (Logic):** Finalisasi data class `Menu.kt` (id, nama, harga, deskripsi).
*   **Output:** Tampilan awal muncul dengan tema yang benar dan model data sudah fix.

### ⚙️ PHASE 3: Core CRUD Logic (Target: Fungsi CRUD Jalan) ✅
**Penanggung Jawab: Musa**
*   [x] Buat class/helper untuk interaksi Firebase (`MenuRepository.kt`).
*   [x] Implementasi fungsi `createMenu()`.
*   [x] Implementasi fungsi `readMenus()` (menggunakan Listener).
*   [x] Implementasi fungsi `updateMenu()` & `deleteMenu()`.
*   **Output:** Logika penambahan, pengambilan, pengubahan, dan penghapusan data siap digunakan.

### 🔗 PHASE 4: UI Integration (Target: Fitur Bisa Dipakai User) 🚧
**Penanggung Jawab: Dani & Musa**
*   [ ] **Dani:** Selesaikan layout `item_menu.xml`, `activity_add_menu.xml`, dan `activity_edit_menu.xml`.
*   [ ] **Musa & Dani:** Hubungkan Firebase ke `RecyclerView`.
*   [ ] **Musa & Dani:** Hubungkan tombol "Tambah" dan "Edit" ke Activity masing-masing.
*   **Output:** User bisa menambah, melihat, mengedit, dan menghapus menu melalui layar HP.

### 🛠️ PHASE 5: Testing & UX Polish (Target: App Stabil)
**Penanggung Jawab: Mazaya**
*   [ ] Uji coba input (Validasi: Harga tidak boleh kosong, Nama harus diisi).
*   [ ] Tambahkan Dialog Konfirmasi sebelum menghapus data.
*   [ ] Handling error saat internet mati.
*   [ ] Pastikan tidak ada *force close* saat aplikasi digunakan.
*   **Output:** Aplikasi minim bug dan nyaman digunakan.

### 📝 PHASE 6: Documentation & Finalization (Target: Siap Kumpul)
**Penanggung Jawab: Andhika**
*   [ ] Pengumpulan screenshot setiap fitur.
*   [ ] Pembuatan presentasi (PPT) yang menarik.
*   [ ] Finalisasi file `README.md` (Update status fitur).
*   [ ] Penataan folder project agar rapi.
*   **Output:** Laporan lengkap dan project siap dipresentasikan.

---

## 👥 TIM PENGEMBANG
*   **Farrel:** Cloud Architect & Firebase Setup
*   **Musa:** Logic Developer (Kotlin)
*   **Dani:** UI/UX Designer (XML)
*   **Mazaya:** Quality Assurance (Testing)
*   **Andhika:** Documentation Specialist

---

## 🧠 FLOW DATA
`Input UI` ➔ `Kotlin Logic` ➔ `Firebase Realtime DB` ➔ `Sync to All Users (Realtime)`
