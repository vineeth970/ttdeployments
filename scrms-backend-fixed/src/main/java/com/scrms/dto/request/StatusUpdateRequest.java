package com.scrms.dto.request;

import com.scrms.enums.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private ComplaintStatus status;

    private String remarks;

    private String resolutionNote;

	public ComplaintStatus getStatus() {
		return status;
	}

	public void setStatus(ComplaintStatus status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getResolutionNote() {
		return resolutionNote;
	}

	public void setResolutionNote(String resolutionNote) {
		this.resolutionNote = resolutionNote;
	}

}
