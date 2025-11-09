package backend.stamp.stamp.service.qr;

import backend.stamp.coupon.entity.Coupon;
import backend.stamp.coupon.repository.CouponRepository;
import backend.stamp.global.exception.ApplicationException;
import backend.stamp.global.exception.ErrorCode;
import backend.stamp.stamp.entity.Stamp;
import backend.stamp.stamp.repository.StampRepository;
import backend.stamp.store.entity.Store;
import backend.stamp.store.repository.StoreRepository;
import backend.stamp.users.entity.Users;
import backend.stamp.users.repository.UsersRepository;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class QRCodeService {
    private final CouponRepository couponRepository;
    private final UsersRepository usersRepository;
    private final StoreRepository storeRepository;

    /**
     * QR 코드를 storeCode로 변환
     * @param file
     * @return
     * @throws Exception
     */
    public String decodeQRCode(MultipartFile file) throws Exception {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(bitmap);
        return result.getText(); // ex: "store_102"
    }

    /**
     * 적립 로직
     * @param storeCode
     * @param userId
     * @throws ApplicationException
     */
    public void addStamp(String storeCode, Long userId) throws ApplicationException {
        Optional<Store> storeOpt = storeRepository.findByName(storeCode);
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        if (storeOpt.isEmpty()) {
            throw new ApplicationException(ErrorCode.INVALIDE_QRCODE);
        }
        Coupon coupon = new Coupon();
        coupon.setName(storeCode);
        coupon.setUsers(user);
        couponRepository.save(coupon);

        user.setStampSum(user.getStampSum()+1);
        usersRepository.save(user);
    }
}
