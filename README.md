# 🍽️ PROYEK: MenuKita
Aplikasi Manajemen Menu Restoran berbasis Android dengan **Firebase Realtime Database**.

---

## 🎨 TEMA: WARM FOOD ORDERING (UPDATE)
*   **Primary:** `#FF6B35` (Warm Orange/Red)
*   **Secondary:** `#F9F7F4` (Cream/Beige)
*   **Accent:** `#2ECC71` (Fresh Green / CTA)
*   **Neutral Dark:** `#2C3E50` (Text/Heading)
*   **Neutral Light:** `#ECEFF1` (Border/Divider)
*   **Background:** Gradient cream -> light orange

## 🖌️ ARAH DESAIN (Sesuai Konsep Baru)
*   **Style:** modern, clean, appetizing, fokus pada food photography.
*   **Komponen utama:** card menu, promo banner carousel, category tabs, cart badge.
*   **Button:** primary hijau (CTA), secondary outline orange.
*   **Cards:** radius 12px, shadow halus, border light.
*   **Interaksi:** hover scale 1.02, press scale 0.98, transition 0.3s.

## 📱 SCREEN UTAMA (Ringkas)
*   **Login/Register:** form card centered, gradient background, link "Lupa Password".
*   **Home/Dashboard:** header + search + cart, promo banner, kategori tab, menu grid 2 kolom.
*   **Detail Menu (Modal):** foto besar, opsi custom, quantity counter, CTA tambah keranjang.
*   **Checkout:** multi-step (Review -> Delivery -> Payment -> Confirm).
*   **Order Confirmation:** status timeline + estimasi waktu.
*   **Profile:** info user, histori order singkat, settings, ganti password.
---

## 🚀 PANDUAN SETUP AWAL (WAJIB) ⚠️
Agar aplikasi bisa terhubung ke database, lakukan langkah ini setelah `git clone`:
1.  **Download Config:** Ambil file `google-services.json` dari Firebase Console (Project MenuKita).
2.  **Pindahkan File:** Simpan di folder `app/google-services.json`.
3.  **Sync Gradle:** Klik ikon gajah (**Sync Project with Gradle Files**).

---

## 📅 ROADMAP PENGERJAAN (UPDATE)

### 🏗️ PHASE 1: Foundation & Connectivity ✅
*   [x] Setup Firebase & Google Services.
*   [x] Konfigurasi Realtime Database URL & Rules.

### 🎨 PHASE 2: UI Base & Data Modeling ✅
*   [x] **Update Tema:** Migrasi ke Warm Food Ordering (orange/cream/green).
*   [x] **Model Data:** Finalisasi `Menu.kt` dengan dukungan Null Safety.

### ⚙️ PHASE 3: Core Logic & Repository ✅
*   [x] **Architecture:** Implementasi Repository Pattern (`MenuRepository.kt`).
*   [x] **Logic:** Full CRUD (Create, Read Realtime, Update, Delete).

### 🔗 PHASE 4: UI Integration & Adapter ✅
*   [x] **View Binding:** Migrasi dari findViewById ke View Binding untuk stabilitas.
*   [x] **RecyclerView:** Implementasi `MenuAdapter` dengan format mata uang Rupiah.
*   [x] **Navigation:** Integrasi antar Activity (Main, Add, Edit).

### 🛠️ PHASE 5: Testing & UX Polish ✅
*   [x] **Validasi:** Error handling pada input kosong atau harga tidak valid.
*   [x] **Network:** Deteksi koneksi internet otomatis sebelum aksi database.
*   [x] **UX:** Dialog konfirmasi hapus data.

---

## 🚀 REKOMENDASI PHASE SELANJUTNYA (UPGRADE)

### 🧾 PHASE 6: Pesan & Bayar (Order & Payment)
*   [x] **Cart & Checkout:** User bisa pilih menu, atur jumlah, lalu checkout.
*   [x] **Order Model:** Tambahkan model `Order` (items, total, status, userId).
*   [x] **Status Pesanan:** Status dasar (Menunggu, Diproses, Selesai, Dibatalkan).
*   [x] **Metode Pembayaran:** Cash/Transfer sebagai tahap awal.
*   [x] **Riwayat Pesanan:** User bisa melihat histori order per akun.
*   [x] **Profil Akun (User & Admin):** Ubah nama, email, dan password langsung dari aplikasi.

### 📸 PHASE 7: Image Integration (Fitur Foto)
*   [x] Menampilkan foto menu via **URL gambar** (tanpa Firebase Storage).
*   [x] Menggunakan library **Glide** untuk menampilkan gambar menu secara asinkron.

### 🔍 PHASE 8: Search & Category (Filter Data)
*   [x] Fitur pencarian menu berdasarkan nama.
*   [x] Penambahan kategori menu (Makanan, Minuman, Dessert).

### 🔐 PHASE 9: User Authentication (Login)
*   [x] Integrasi **Firebase Auth** (email & password).
*   [x] Login/Registrasi menggunakan Firebase Auth (tanpa simpan password di DB).

---

## 💡 REKOMENDASI FITUR BARU (IDE)
*   **Promo & Diskon:** Kupon atau potongan harga untuk menu tertentu.
*   **Best Seller & Featured:** Tandai menu favorit dan tampilkan di bagian atas.
*   **Stok & Ketersediaan:** Menu bisa ditandai habis untuk menghindari order.
*   **Rating & Review:** User bisa memberi penilaian pada menu.
*   **Notifikasi:** Informasi status pesanan atau promo baru.

---

## 👥 TIM PENGEMBANG
*   **Farrel:** Cloud Architect & Firebase Setup
*   **Musa:** Logic & Backend Developer (Kotlin) - *Penyelesai Task Dasar & CRUD*
*   **Dani:** UI/UX Designer (XML)
*   **Mazaya:** Quality Assurance (Testing)
*   **Andhika:** Documentation Specialist


