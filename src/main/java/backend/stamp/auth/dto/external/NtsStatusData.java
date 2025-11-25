package backend.stamp.auth.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NtsStatusData {
    @JsonProperty("b_no")
    private String businessNumber;

    @JsonProperty("b_stt")
    private String status;          // 계속사업자, 폐업자, 국세청에 등록되지 않은 사업자등록번호입니다. 등

    @JsonProperty("b_stt_cd")
    private String statusCode;

    @JsonProperty("tax_type")
    private String taxType;         // 부가가치세 일반과세자, 국세청에 등록되지 않은 사업자등록번호입니다. 등

    @JsonProperty("tax_type_cd")
    private String taxTypeCode;

    @JsonProperty("end_dt")
    private String closedDate;

    @JsonProperty("utcc_yn")
    private String utccYn;

    @JsonProperty("tax_type_change_dt")
    private String taxTypeChangeDate;

    @JsonProperty("invoice_apply_dt")
    private String invoiceApplyDate;

    @JsonProperty("rbf_tax_type")
    private String previousTaxType;

    @JsonProperty("rbf_tax_type_cd")
    private String previousTaxTypeCode;
}
