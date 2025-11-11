package backend.stamp.stamp.service.qr;

import backend.stamp.coupon.entity.Coupon;
import backend.stamp.coupon.repository.CouponRepository;
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

    /**
     * 적립 로직
     * @param storeId
     * @param userId
     * @throws ApplicationException
     */
    public void addStamp(Long storeId, Long userId) throws ApplicationException {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALIDE_QRCODE));
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        Order recentOrder = orderRepository.findTopByUsersAndStoreOrderByOrderDateDesc(user, store)
                .orElse(null); // 주문 없어도 null 허용
        // 기존에 해당 유저가 해당 가게에서 쌓은 스탬프 가져오기 (optional)
        Stamp stamp = stampRepository.findByStoreAndUsers(store, user)
                .orElse(new Stamp());

        if (stamp.getId() == null) {
            stamp.setStore(store);
            stamp.setOrder(recentOrder);
            stamp.setUsers(user);
            stamp.setCurrentCount(0);
            stamp.setDate(LocalDateTime.now());
            stamp.setName(store.getName());
        }

        stamp.setCurrentCount(stamp.getCurrentCount() + 1);
        stampRepository.save(stamp);

        user.setStampSum(user.getStampSum() + 1);
        usersRepository.save(user);
    }

    /**
     * 점주 QR코드 생성 로직
     * @param storeCode
     * @return
     * @throws Exception
     */

    public String generateQRCode(String storeCode) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(storeCode, BarcodeFormat.QR_CODE, 300, 300);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "png", baos);

        byte[] imageBytes = baos.toByteArray();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
}
