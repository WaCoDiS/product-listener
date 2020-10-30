package de.wacodis.productlistener.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.UUID;
import org.joda.time.DateTime;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * message to indicate a job execution failed 
 */
@ApiModel(description = "message to indicate a job execution failed ")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-10-30T14:16:51.226+01:00[Europe/Berlin]")

public class WacodisJobFailed  implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("wpsJobIdentifier")
  private String wpsJobIdentifier = null;

  @JsonProperty("reason")
  private String reason = null;

  @JsonProperty("created")
  private DateTime created = null;

  @JsonProperty("wacodisJobIdentifier")
  private UUID wacodisJobIdentifier = null;

  @JsonProperty("singleExecutionJob")
  private Boolean singleExecutionJob = false;

  @JsonProperty("finalJobProcess")
  private Boolean finalJobProcess = true;

  public WacodisJobFailed wpsJobIdentifier(String wpsJobIdentifier) {
    this.wpsJobIdentifier = wpsJobIdentifier;
    return this;
  }

  /**
   * wps job identifier 
   * @return wpsJobIdentifier
  **/
  @ApiModelProperty(required = true, value = "wps job identifier ")
  @NotNull


  public String getWpsJobIdentifier() {
    return wpsJobIdentifier;
  }

  public void setWpsJobIdentifier(String wpsJobIdentifier) {
    this.wpsJobIdentifier = wpsJobIdentifier;
  }

  public WacodisJobFailed reason(String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * status message describing the reason of failure 
   * @return reason
  **/
  @ApiModelProperty(required = true, value = "status message describing the reason of failure ")
  @NotNull


  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public WacodisJobFailed created(DateTime created) {
    this.created = created;
    return this;
  }

  /**
   * time on which the execution has been identified as failed 
   * @return created
  **/
  @ApiModelProperty(required = true, value = "time on which the execution has been identified as failed ")
  @NotNull

  @Valid

  public DateTime getCreated() {
    return created;
  }

  public void setCreated(DateTime created) {
    this.created = created;
  }

  public WacodisJobFailed wacodisJobIdentifier(UUID wacodisJobIdentifier) {
    this.wacodisJobIdentifier = wacodisJobIdentifier;
    return this;
  }

  /**
   * wacodis job identifer (from WacodisJobDefinition, not wps job identifier!) 
   * @return wacodisJobIdentifier
  **/
  @ApiModelProperty(required = true, value = "wacodis job identifer (from WacodisJobDefinition, not wps job identifier!) ")
  @NotNull

  @Valid

  public UUID getWacodisJobIdentifier() {
    return wacodisJobIdentifier;
  }

  public void setWacodisJobIdentifier(UUID wacodisJobIdentifier) {
    this.wacodisJobIdentifier = wacodisJobIdentifier;
  }

  public WacodisJobFailed singleExecutionJob(Boolean singleExecutionJob) {
    this.singleExecutionJob = singleExecutionJob;
    return this;
  }

  /**
   * indicates if finished wacodis job is single execution job (SingleJobExecutionEvent) 
   * @return singleExecutionJob
  **/
  @ApiModelProperty(required = true, value = "indicates if finished wacodis job is single execution job (SingleJobExecutionEvent) ")
  @NotNull


  public Boolean getSingleExecutionJob() {
    return singleExecutionJob;
  }

  public void setSingleExecutionJob(Boolean singleExecutionJob) {
    this.singleExecutionJob = singleExecutionJob;
  }

  public WacodisJobFailed finalJobProcess(Boolean finalJobProcess) {
    this.finalJobProcess = finalJobProcess;
    return this;
  }

  /**
   * indicates if last (sub-) process of Wacodis Job execution 
   * @return finalJobProcess
  **/
  @ApiModelProperty(required = true, value = "indicates if last (sub-) process of Wacodis Job execution ")
  @NotNull


  public Boolean getFinalJobProcess() {
    return finalJobProcess;
  }

  public void setFinalJobProcess(Boolean finalJobProcess) {
    this.finalJobProcess = finalJobProcess;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WacodisJobFailed wacodisJobFailed = (WacodisJobFailed) o;
    return Objects.equals(this.wpsJobIdentifier, wacodisJobFailed.wpsJobIdentifier) &&
        Objects.equals(this.reason, wacodisJobFailed.reason) &&
        Objects.equals(this.created, wacodisJobFailed.created) &&
        Objects.equals(this.wacodisJobIdentifier, wacodisJobFailed.wacodisJobIdentifier) &&
        Objects.equals(this.singleExecutionJob, wacodisJobFailed.singleExecutionJob) &&
        Objects.equals(this.finalJobProcess, wacodisJobFailed.finalJobProcess);
  }

  @Override
  public int hashCode() {
    return Objects.hash(wpsJobIdentifier, reason, created, wacodisJobIdentifier, singleExecutionJob, finalJobProcess);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WacodisJobFailed {\n");
    
    sb.append("    wpsJobIdentifier: ").append(toIndentedString(wpsJobIdentifier)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
    sb.append("    wacodisJobIdentifier: ").append(toIndentedString(wacodisJobIdentifier)).append("\n");
    sb.append("    singleExecutionJob: ").append(toIndentedString(singleExecutionJob)).append("\n");
    sb.append("    finalJobProcess: ").append(toIndentedString(finalJobProcess)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

