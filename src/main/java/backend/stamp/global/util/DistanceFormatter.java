package backend.stamp.global.util;

//거리 계산 함수
public class DistanceFormatter {

    public static String formatDistance(double meters) {
        if (meters < 1000) {
            return String.format("%dm", (int) meters);
        } else {
            return String.format("%.1fkm", meters / 1000);
        }
    }
}
