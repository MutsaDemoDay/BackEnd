package backend.stamp.stamp.service.qr;

import backend.stamp.coupon.entity.Coupon;
import backend.stamp.coupon.repository.CouponRepository;
import backend.stamp.coupon.service.CouponService;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.order.entity.Order;
import backend.stamp.order.repository.OrderRepository;
import backend.stamp.stamp.entity.Stamp;
import backend.stamp.stamp.repository.StampRepository;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@AllArgsConstructor
public class QRCodeService {
    private final StampRepository stampRepository;
    private final UsersRepository usersRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final CouponService couponService;

    /**
     * 적립 로직
     * @param storeId
     * @param userId
     * @throws ApplicationException
     */
    public void addStamp(Long storeId, Long userId, int stampCount) throws ApplicationException {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALIDE_QRCODE));
        Users users = usersRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        Order recentOrder = orderRepository.findTopByUsersAndStoreOrderByOrderDateDesc(users, store)
                .orElse(null); // 주문 없어도 null 허용
        Stamp stamp = stampRepository.findByStoreAndUsers(store, users)
                .orElse(new Stamp());

        if (stamp.getId() == null) {
            stamp.setStore(store);
            stamp.setOrder(recentOrder);
            stamp.setUsers(users);
            stamp.setCurrentCount(0);
            stamp.setDate(LocalDateTime.now());
            stamp.setName(store.getName());
        }
        int updatedCount = stamp.getCurrentCount() + stampCount;
        stamp.setCurrentCount(updatedCount);
        stampRepository.save(stamp);
        users.setTotalStampSum(users.getTotalStampSum() + stampCount);
        creatCouponByQR(updatedCount, store, users, stamp, recentOrder);
        usersRepository.save(users);
    }
    public void creatCouponByQR(int updatedCount, Store store, Users users, Stamp stamp, Order order) {
        int maxCount = store.getMaxCount();
        while (updatedCount >= maxCount) {
            couponService.createCoupon(users, store);
            users.setCouponNum(users.getCouponNum() + 1);
            updatedCount -= maxCount;
        }
        stamp.setCurrentCount(updatedCount);
        stamp.setOrder(order);
        stampRepository.save(stamp);

        order.setUsed(true);
        orderRepository.save(order);
        usersRepository.save(users);
    }

    /**
     * 유저 QR코드 생성 로직
     * @param email
     * @return
     * @throws Exception
     */

    public String generateQRCode(String email) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(email, BarcodeFormat.QR_CODE, 300, 300);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "png", baos);

        byte[] imageBytes = baos.toByteArray();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
}
