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

## 🎯 ROADMAP UPGRADE UI/UX (MODERN & ATTRACTIVE)

### 🎨 PHASE 10: Design System & Component Modernization
**Tujuan:** Membangun design system yang konsisten dan komponen UI yang modern.
- [ ] **Design System Foundation**
  - [x] Update palet warna dengan modern gradient colors
  - [x] Definisi typography scale (size, weight, line-height)
  - [x] Spacing system (8px base, scaling factors)
  - [x] Border radius dan shadow system yang konsisten
  - [x] Dokumentasi design guidelines

- [ ] **Material 3 Design Icons & Components**
  - [ ] Implementasi Material Icons untuk konsistensi
  - [x] Update button styles (elevated, filled, outlined, text)
  - [x] Modern card components dengan hover effects
  - [x] Improve form input dengan focus states yang jelas
  - [ ] Update checkbox, radio button, dan toggle switches

- [x] **Modern Color & Gradient Implementation**
  - [x] Gradient backgrounds untuk hero sections
  - [x] Dynamic color overlays untuk food photography
  - [x] Smooth color transitions pada interaksi
  - [x] Light & dark mode color variants

---

### ✨ PHASE 11: Homepage & Feed UI Refresh
**Tujuan:** Redesign homepage dengan tampilan yang engaging dan modern.
- [x] **Hero Section**
  - [x] Dynamic banner carousel dengan parallax effect
  - [x] Animated promotional badges dan chips
  - [x] Search bar dengan autocomplete suggestions
  - [x] Floating action button untuk quick actions

- [ ] **Menu Grid & Listings**
  - [x] Update card design (food image, name, price, rating, tag)
  - [x] Staggered grid layout atau masonry style
  - [x] Skeleton loading untuk better UX
  - [x] Smooth scroll animations saat load data
  - [x] Pull-to-refresh functionality

- [x] **Category & Filter Chips**
  - [x] Animated category selector dengan ripple effect
  - [x] Smooth transition saat switch category
  - [x] Advanced filter panel dengan animations
  - [x] Save favorite categories untuk user

---

### 🎭 PHASE 12: Advanced Animations & Micro-interactions
**Tujuan:** Tambahkan animations smooth untuk meningkatkan visual appeal.
- [x] **Button & Touch Feedback**
  - [x] Ripple effect pada semua interactive elements
  - [x] Smooth press animations (0.08s scale down)
  - [x] Visual feedback untuk loading states
  - [x] Success animations setelah aksi tertentu

- [x] **Screen Transitions & Navigation**
  - [x] Shared element transition untuk detail page
  - [x] Slide-in animations untuk modals & bottom sheets
  - [x] Fade-in animations untuk list items
  - [x] Smooth navigation stack animations

- [x] **Activity Indicators & Progress**
  - [x] Modern loading spinners dengan custom design
  - [x] Smooth progress bars dengan gradient
  - [x] Step indicators dengan smooth transitions
  - [x] Skeleton screens untuk data loading

- [x] **Gesture & Swipe Interactions**
  - [x] Swipe-to-delete dengan undo action
  - [x] Swipe-to-favorite functionality
  - [x] Long-press context menus dengan animations
  - [x] Drag-and-drop untuk cart items

---

### 🛒 PHASE 13: Cart & Checkout UI Enhancement
**Tujuan:** Modernisasi proses order dengan UX yang intuitif dan menarik.
- [ ] **Cart Screen Redesign**
  - [x] Modern mini cart badge dengan counter
  - [ ] Improved cart drawer/sheet dengan swipe gestures
  - [x] Item cards with quantity controls (+ - buttons)
  - [ ] Clear price breakdown dengan animation
  - [ ] Empty state illustration & CTA

- [ ] **Checkout Flow Enhancement**
  - [x] Multi-step checkout dengan visual progress indicator
  - [ ] Smooth transitions antara steps
  - [ ] Address selector dengan Google Maps integration (future)
  - [x] Payment method selector dengan icons
  - [ ] Order summary dengan collapsible details

- [ ] **Order Confirmation & Timeline**
  - [x] Success animation dengan confetti effect
  - [x] Beautiful order timeline dengan status badges
  - [x] Estimated delivery time display
  - [x] Share order & track order functionality
  - [ ] Customer support quick contact button

---

### 📱 PHASE 14: Details & Product Page Redesign
**Tujuan:** Create immersive product experience dengan galeri foto dan informasi detail.
- [ ] **Product Image Showcase**
  - [x] Full-screen image gallery dengan pinch-to-zoom
  - [x] Thumbnail carousel di bawah
  - [x] Image lazy loading & caching optimization
  - [ ] Image zoom animation pada tap

- [ ] **Product Information Layout**
  - [x] Sticky header saat scroll
  - [x] Beautiful typography untuk judul & deskripsi
  - [ ] Rating & review component dengan animations
  - [x] Ingredient tags dengan icons
  - [ ] Nutritional information display (future)

- [ ] **Product Actions**
  - [x] Quantity selector dengan stepper animation
  - [x] Large prominent "Add to Cart" button
  - [x] Wishlist/favorite button dengan heart animation
  - [x] Share menu functionality
  - [ ] Smooth bottom sheet untuk customization options

---

### 🎨 PHASE 15: Profile & Settings UI Modernization
**Tujuan:** Redesign user profile dengan modern material design principles.
- [ ] **Profile Header**
  - [x] Large avatar dengan status badge
  - [x] User info section dengan edit capability
  - [ ] Achievement/loyalty badges display
  - [x] Quick stats (total orders, member since, rewards points)

- [ ] **Menu Items & Settings**
  - [x] Modern list items dengan trailing widgets
  - [x] Segmented settings sections dengan icons
  - [x] Toggle switches dengan smooth animations
  - [x] Dividers & spacing sesuai material guidelines

- [ ] **Advanced Profile Features**
  - [x] Addresses management dengan add/edit/delete
  - [x] Payment methods storage dengan icons
  - [ ] Order history dengan filter options
  - [x] Wishlist/favorite items collection
  - [x] Account security settings

---

### 🌙 PHASE 16: Dark Mode & Accessibility
**Tujuan:** Support dark mode dan improve accessibility untuk semua users.
- [ ] **Dark Mode Implementation**
  - [ ] Dynamic color theme berdasarkan device preference
  - [x] Dark variants untuk semua components
  - [ ] Updated images & graphics untuk dark mode
  - [x] Settings toggle untuk switching theme
  - [ ] Smooth transition saat switch mode

- [ ] **Accessibility Improvements**
  - [ ] Sufficient color contrast untuk text
  - [x] Content descriptions untuk images
  - [ ] Keyboard navigation support
  - [ ] Screen reader compatibility
  - [ ] Testing dengan accessibility tools

---

### ⚡ PHASE 17: Performance & Polish
**Tujuan:** Optimize performance dan final visual polish.
- [ ] **Performance Optimization**
  - [x] Database query optimization
  - [x] Image compression & caching strategy
  - [ ] Lazy loading untuk list items
  - [ ] Memory leak fixes
  - [ ] Monitoring dengan Firebase Performance Monitoring

- [ ] **Final UI Polish**
  - [ ] Consistency check across all screens
  - [x] Error states & empty states design
  - [x] Bottom navigation bar modernization
  - [ ] No pixel gap issues & alignment check
  - [ ] Toast & snackbar animations

- [ ] **QA & Testing**
  - [ ] Manual UI testing di berbagai devices & sizes
  - [ ] Screenshot testing untuk regressions
  - [ ] Performance profiling dengan Android Profiler
  - [ ] User testing & feedback incorporation

---

### 🎯 PHASE 18: Onboarding & User Welcome Experience
**Tujuan:** Create engaging first-time user experience dengan tutorial interaktif.
- [x] **Splash Screen & Loading**
  - [x] Animated splash screen dengan brand logo
  - [x] Smooth transition ke home screen
  - [x] Loading animation dengan lottie animations
  - [x] Brand story animation

- [ ] **Onboarding Screens**
  - [x] Multi-page carousel onboarding (3-5 screens)
  - [ ] Beautiful illustrations/animations untuk setiap screen
  - [x] Skip & Next button dengan smooth transitions
  - [ ] Progress indicators dengan animation
  - [x] Final CTA "Get Started" dengan emphasis

- [x] **Interactive Tutorial**
  - [x] Tooltip guides untuk fitur utama
  - [x] Highlight effect pada key features
  - [x] Smooth animations saat pointing features
  - [x] Skip tutorial option untuk experienced users

---

### 🎨 PHASE 19: Icons, Illustrations & Custom Graphics
**Tujuan:** Implement custom icons & illustrations untuk visual consistency.
- [ ] **Icon System**
  - [ ] Custom icon set design untuk semua features
  - [ ] Consistent stroke width & style
  - [ ] Icon animations (rotate, scale, bounce)
  - [ ] Icon states (normal, active, disabled, loading)
  - [ ] SVG implementation untuk scalability

- [ ] **Illustrations & Graphics**
  - [ ] Custom food illustrations untuk empty states
  - [ ] Error state illustrations dengan playful tone
  - [ ] Success animations dengan celebratory graphics
  - [ ] Background patterns & decorative elements
  - [ ] Lottie animations untuk complex interactions

- [ ] **Branded Assets**
  - [ ] Restaurant logo optimization untuk berbagai ukuran
  - [ ] Badge & seal designs untuk best seller
  - [ ] Status badges dengan custom design
  - [ ] Loyalty program badges & achievements

---

### 💬 PHASE 20: Bottom Navigation & FAB Modern Design
**Tujuan:** Modernize navigation elements dengan trendy designs.
- [x] **Bottom Navigation Bar**
  - [x] Glassmorphism atau modern gradient design
  - [x] Active indicator dengan smooth animation
  - [x] Badge notifications dengan animations
  - [x] Swipe navigation support
  - [x] Icon color change saat active dengan smooth transition

- [x] **Floating Action Button (FAB)**
  - [x] Main FAB dengan expansion menu untuk quick actions
  - [x] FAB morphing animations (transform antara berbagai icons)
  - [x] FAB position optimization untuk safe area
  - [x] Multiple FABs dengan staggered animation
  - [x] Haptic feedback saat tap

- [x] **Navigation Drawer (Side Menu)**
  - [x] Modern drawer design dengan header section
  - [x] Smooth slide-in/out animations
  - [x] User profile quick access di bagian atas
  - [x] Modern menu items dengan icons
  - [x] Settings & logout dengan confirmation dialogs

---

### ✨ PHASE 21: Cards, Containers & Modern Layouts
**Tujuan:** Implement modern card designs dengan glassmorphism & neumorphism trends.
- [ ] **Card Variants**
  - [x] Elevated cards dengan shadow gradients
  - [x] Outlined cards dengan modern borders
  - [x] Filled cards dengan solid backgrounds
  - [ ] Container cards dengan subtle gradients
  - [x] Cards dengan hover & click effects

- [ ] **Glassmorphism Elements**
  - [x] Frosted glass effect pada overlay components
  - [ ] Semi-transparent backgrounds dengan blur
  - [ ] Light backdrop effect untuk modals
  - [x] Modern aesthetic untuk floating elements

- [ ] **Layout Improvements**
  - [x] Improved spacing consistency (8px grid)
  - [x] Modern aspect ratios untuk images & cards
  - [ ] Smart padding untuk safe area handling
  - [x] Flexible layouts untuk berbagai screen sizes
  - [x] Modern grid & column system

---

### 📸 PHASE 22: Advanced Image Handling & Parallax Effects
**Tujuan:** Modern image techniques untuk visual impact yang lebih besar.
- [ ] **Image Gallery & Showcase**
  - [x] Full-screen image viewer dengan pinch-to-zoom
  - [x] Swipe navigation antar images
  - [ ] Smooth zoom transitions saat tap
  - [ ] Image blur transitions
  - [ ] Share image functionality dengan social integration

- [ ] **Parallax & Scroll Effects**
  - [x] Parallax motion pada hero images
  - [ ] Scroll-driven animations untuk content reveal
  - [ ] Image fade-in saat scroll ke view
  - [x] Nested scrolling untuk complex layouts
  - [ ] Smooth momentum scrolling

- [ ] **Image Processing**
  - [ ] Dynamic overlay untuk improved readability
  - [ ] Color overlay based on image dominance
  - [ ] Image blur backdrop untuk contrast
  - [ ] Responsive image sizing untuk performance
  - [ ] Placeholder blur images saat loading

---

### 🎭 PHASE 23: Loading States & Skeleton Screens
**Tujuan:** Replace boring loaders dengan modern & engaging alternatives.
- [x] **Advanced Loading Animations**
  - [x] Custom circular progress indicators
  - [x] Wave loading animations
  - [x] Pulse effect untuk breathing animations
  - [x] Multi-layer loaders dengan staggered timing
  - [x] Color-changing loaders dengan gradients

- [ ] **Skeleton Screens**
  - [x] Shimmer effect animations
  - [x] Matching skeleton layouts untuk setiap content
  - [x] Smooth transition dari skeleton ke actual content
  - [ ] Gradient pulses untuk compelling effect
  - [x] Custom colored skeletons sesuai theme

- [ ] **Empty States**
  - [x] Friendly illustrations untuk empty cart, empty orders, dll
  - [ ] Animated empty state graphics dengan lottie
  - [ ] Supportive copy dengan helpful CTAs
  - [x] Animation triggers saat screen load
  - [ ] Animated buttons untuk actions

---

### 🎪 PHASE 24: Special Effects & Seasonal Themes
**Tujuan:** Add wow factor dengan special effects dan dynamic theming.
- [ ] **Seasonal Themes**
  - [ ] Holiday special themes (New Year, Ramadan, Christmas, etc)
  - [ ] Theme switching dengan smooth transitions
  - [x] Seasonal color variants
  - [ ] Limited edition design assets
  - [x] Toggleable seasonal effects

- [x] **Celebration Effects**
  - [x] Confetti animations untuk order success
  - [x] Fireworks effect untuk achievements
  - [x] Rain/snow effects untuk seasonal events
  - [x] Particle effects untuk interactive elements
  - [x] Toast notifications dengan custom animations

- [x] **Interactive Effects**
  - [x] Floating particles di background
  - [x] Shimmer effects pada promotional items
  - [x] Glow effects untuk featured products
  - [x] Gradient animated backgrounds
  - [x] Tilt/3D perspective effects pada cards

---

### 🌈 PHASE 25: Typography & Text Styling Excellence
**Tujuan:** Modern typography untuk improved readability & aesthetics.
- [ ] **Typography System**
  - [x] Establish clear hierarchy (Display, Headline, Title, Body, Label)
  - [ ] Dynamic font scaling untuk responsive design
  - [x] Perfect line height untuk readability
  - [x] Letter spacing optimization
  - [x] Font weight variations (Light, Regular, Medium, Bold)

- [x] **Text Effects & Animations**
  - [x] Gradient text untuk emphasis
  - [x] Animated text reveal animations
  - [x] Typewriter effect untuk promotional text
  - [x] Color interpolation saat highlight
  - [x] Smooth text transitions

- [x] **Content Styling**
  - [x] Rich text formatting dengan consistent styling
  - [x] Quote/testimonial styling dengan visual design
  - [x] Price typography dengan size hierarchy
  - [x] Highlighted text dengan background chips
  - [x] Strikethrough untuk old prices

---

### 🔔 PHASE 26: Notifications & Toast Redesign
**Tujuan:** Modern notification system dengan better visibility & UX.
- [ ] **Push Notifications**
  - [ ] Rich notification layouts dengan images
  - [ ] Custom notification styles per type (success, error, info)
  - [ ] Notification sound & vibration custom patterns
  - [ ] Action buttons pada notifications
  - [ ] Notification history & archive

- [ ] **In-App Notifications**
  - [x] Modern toast designs dengan icons
  - [x] Snackbar dengan action buttons
  - [ ] Top banner notifications untuk urgent info
  - [ ] Bottom sheet notifications untuk detailed info
  - [ ] Smooth entrance & exit animations

- [ ] **Alert Dialogs & Modals**
  - [ ] Modern dialog designs dengan rounded corners
  - [ ] Bottom sheet style modals dengan swipe-to-dismiss
  - [ ] Confirmation dialogs dengan affirmative CTAs
  - [ ] Loading dialogs dengan smooth transitions
  - [ ] Error dialogs dengan helpful solutions

---

### 🎖️ PHASE 27: Loyalty & Gamification UI
**Tujuan:** Engage users dengan loyalty program & gamification elements.
- [ ] **Loyalty Program Visual**
  - [x] Points progress bar dengan smooth animation
  - [ ] Tier badges dengan achievement unlocks
  - [ ] Reward cards dengan unlock animation
  - [ ] Point history dengan timeline view
  - [ ] Referral tracking dashboard

- [ ] **Gamification Elements**
  - [ ] Achievement badges dengan unlock animation
  - [ ] Streak counters dengan celebration effects
  - [ ] Progress levels dengan milestone markers
  - [ ] Leaderboard/rankings dengan animations
  - [ ] Daily challenges dengan progress indicators

- [ ] **Reward Redemption UI**
  - [ ] Beautiful reward catalog dengan cards
  - [ ] Redemption flow dengan confirmation
  - [ ] Used reward badges dengan timestamp
  - [ ] Expiring rewards alert dengan urgency indicator
  - [ ] Reward sharing functionality

---

### 🌟 PHASE 28: Micro-interactions & Gesture Improvements
**Tujuan:** Refine small interactions untuk polished user experience.
- [ ] **Haptic Feedback**
  - [x] Haptic response untuk button taps
  - [ ] Vibration patterns untuk different actions
  - [ ] Long-press haptic feedback
  - [ ] Swipe gesture haptic effects

- [ ] **Gesture Enhancements**
  - [x] Double-tap untuk favorite/like functionality
  - [ ] Long-press context menus dengan animations
  - [x] Pinch gesture untuk zoom & scale
  - [ ] Drag handles dengan visual feedback
  - [ ] Gesture recognition dengan smooth animations

- [x] **Advanced Interactions**
  - [x] Parallax scrolling untuk nested lists
  - [x] Spring animations untuk bounce effects
  - [x] Flick gesture untuk quick actions
  - [x] Swipe-back gesture handler
  - [x] Gesture conflict resolution untuk smooth UX

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


