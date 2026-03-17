package com.team2.Crowdsourced_Waste_Collection_Recycling_System.config;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Voucher;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.reward.VoucherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
@Transactional
public class VoucherInit implements CommandLineRunner {

    private final VoucherRepository voucherRepository;

    public VoucherInit(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @Override
    public void run(String... args) {
        LocalDateTime now = LocalDateTime.now();
        for (VoucherSeed seed : seeds()) {
            upsertVoucher(seed, now);
        }
    }

    private void upsertVoucher(VoucherSeed seed, LocalDateTime now) {
        Voucher voucher = voucherRepository.findByTitleIgnoreCase(seed.title())
                .orElseGet(Voucher::new);

        if (voucher.getCreatedAt() == null) {
            voucher.setCreatedAt(now);
        }

        voucher.setTitle(seed.title());
        voucher.setValueDisplay(seed.valueDisplay());
        voucher.setBannerUrl(cleanUrl(seed.bannerUrl()));
        voucher.setLogoUrl(cleanUrl(seed.logoUrl()));
        voucher.setPointsRequired(parsePoints(seed.pointsRequired()));
        voucher.setValidUntil(parseDate(seed.validUntil()));
        voucher.setActive(Boolean.TRUE);
        voucher.setRemainingStock(seed.remainingStock());
        voucher.setTerms(seed.terms() != null ? List.copyOf(seed.terms()) : List.of());
        voucher.setUpdatedAt(now);

        Voucher saved = voucherRepository.save(voucher);
        if (saved.getVoucherCode() == null || saved.getVoucherCode().isBlank()) {
            saved.setVoucherCode(String.format("V%03d", saved.getId()));
            saved.setUpdatedAt(now);
            voucherRepository.save(saved);
        }
    }

    private Integer parsePoints(String raw) {
        if (raw == null) {
            return 0;
        }
        String digits = raw.replaceAll("[^0-9]", "");
        if (digits.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return LocalDate.parse(raw.trim());
    }

    private String cleanUrl(String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.trim();
        if (s.startsWith("`") && s.endsWith("`") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1).trim();
        }
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1).trim();
        }
        if (s.startsWith("'") && s.endsWith("'") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1).trim();
        }
        return s;
    }

    private List<VoucherSeed> seeds() {
        return List.of(
                new VoucherSeed(
                        "Jollibee Voucher - 50,000 VND",
                        "https://jollibee.com.vn/media/catalog/product/cache/9011257231b13517d19d9bae81fd87cc/m/_/m_n_ngon_ph_i_th_-_1.png",
                        "https://upload.wikimedia.org/wikipedia/en/8/84/Jollibee_2011_logo.svg",
                        "50,000 VND",
                        "55,000",
                        "2026-12-31",
                        null,
                        List.of(
                                "Valid for dine-in and takeout only.",
                                "Not applicable for delivery orders.",
                                "One voucher per transaction."
                        )
                ),
                new VoucherSeed(
                        "Phuc Long Voucher - 30,000 VND",
                        "https://s3-hcmc02.higiocloud.vn/phuclong/2025/04/image-20250409083419.png",
                        "https://www.phuclong.com.vn/_next/static/images/logo-ba196fcddcd6f23a70406fd4cf71d422.png",
                        "30,000 VND",
                        "33,000",
                        "2026-11-30",
                        null,
                        List.of(
                                "Applicable for all beverages.",
                                "Valid at all Phuc Long stores nationwide.",
                                "Cannot be combined with other promotions."
                        )
                ),
                new VoucherSeed(
                        "Katinat Voucher - 50,000 VND",
                        "https://katinat.vn/wp-content/uploads/2024/03/image.png",
                        "https://katinat.vn/wp-content/uploads/2023/12/cropped-Kat-Logo-fa-rgb-05__1_-removebg-preview.png",
                        "50,000 VND",
                        "55,000",
                        "2026-12-15",
                        null,
                        List.of(
                                "Valid at all Katinat branches.",
                                "Not redeemable for cash.",
                                "Valid for one-time use only."
                        )
                ),
                new VoucherSeed(
                        "KFC Voucher - 50,000 VND",
                        "https://static.kfcvietnam.com.vn/images/category/lg/MON%20AN%20NHE.jpg?v=LZrXEL",
                        "https://web.archive.org/web/20220716042518im_/https://brasol.vn/public/ckeditor/uploads/thiet-ke-logo-tin-tuc/logo-kfc-png.png",
                        "50,000 VND",
                        "55,000",
                        "2026-12-31",
                        null,
                        List.of(
                                "Valid for all menu items.",
                                "Show QR code at cashier.",
                                "Not valid with other discounts."
                        )
                ),
                new VoucherSeed(
                        "Highlands Coffee Voucher - 100,000 VND",
                        "https://www.highlandscoffee.com.vn/vnt_upload/home/web_banner_2000x2000.jpg",
                        "https://www.highlandscoffee.com.vn/vnt_upload/weblink/red_BG_logo800.png",
                        "100,000 VND",
                        "110,000",
                        "2026-12-31",
                        null,
                        List.of(
                                "Valid for all drinks and food items.",
                                "Minimum bill required.",
                                "Cannot be exchanged for cash."
                        )
                ),
                new VoucherSeed(
                        "Tous Les Jours Voucher - 50,000 VND",
                        "https://cdn.prod.website-files.com/649249d29a20bd6bc3deac48/649249d29a20bd6bc3deae34_TousLesJours_MangoCloudCake.jpg",
                        "https://cdn.prod.website-files.com/649249d29a20bd6bc3deac45/69692c3d9117f3d73ff839fa_4.0%20BI_Logo_Full_Green-p-1080.png",
                        "50,000 VND",
                        "55,000",
                        "2026-10-31",
                        null,
                        List.of(
                                "Applicable for all bakery products.",
                                "Valid for in-store purchases only.",
                                "One voucher per receipt."
                        )
                ),
                new VoucherSeed(
                        "Ding Tea Voucher - 50,000 VND",
                        "https://dingtea.vn/images/thu-3/image_cover.jpg",
                        "https://dingtea.vn/images/logospare.png",
                        "50,000 VND",
                        "55,000",
                        "2026-09-30",
                        null,
                        List.of(
                                "Valid for all drinks.",
                                "Cannot be combined with other promotions.",
                                "Valid nationwide."
                        )
                ),
                new VoucherSeed(
                        "Lotteria Voucher - 50,000 VND",
                        "https://www.lotteria.vn/media/catalog/product/cache/400x400/g/_/g_r_n_ph_n_1_3.jpg.webp",
                        "https://www.lotteria.vn/grs-static/images/logo-white.svg",
                        "50,000 VND",
                        "55,000",
                        "2026-12-31",
                        null,
                        List.of(
                                "Valid for all menu items.",
                                "Not applicable for delivery.",
                                "One-time redemption only."
                        )
                ),
                new VoucherSeed(
                        "Starbucks Voucher - 100,000 VND",
                        "https://content-prod-live.cert.starbucks.com/binary/v2/asset/137-106110.jpg",
                        "https://mondialbrand.com/wp-content/uploads/2023/08/logo-starbucks-y-nghia-va-lich-su-cua-bieu-tuong-ca-phe-nang-tien-ca-tu-1917-8.jpg",
                        "100,000 VND",
                        "110,000",
                        "2026-12-31",
                        null,
                        List.of(
                                "Valid at all Starbucks Vietnam stores.",
                                "Not valid for bottled beverages.",
                                "Cannot be redeemed for cash."
                        )
                )
        );
    }

    private record VoucherSeed(
            String title,
            String bannerUrl,
            String logoUrl,
            String valueDisplay,
            String pointsRequired,
            String validUntil,
            Integer remainingStock,
            List<String> terms
    ) {
    }
}
