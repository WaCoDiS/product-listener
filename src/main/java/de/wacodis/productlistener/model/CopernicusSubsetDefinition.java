package de.wacodis.productlistener.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import de.wacodis.productlistener.model.AbstractSubsetDefinition;
import de.wacodis.productlistener.model.AbstractSubsetDefinitionTemporalCoverage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * contains information for defining a subset definition for Copernicus process inputs
 */
@ApiModel(description = "contains information for defining a subset definition for Copernicus process inputs")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-10-30T14:16:51.226+01:00[Europe/Berlin]")

public class CopernicusSubsetDefinition extends AbstractSubsetDefinition implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * Gets or Sets satellite
   */
  public enum SatelliteEnum {
    _1("sentinel-1"),
    
    _2("sentinel-2"),
    
    _3("sentinel-3");

    private String value;

    SatelliteEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static SatelliteEnum fromValue(String text) {
      for (SatelliteEnum b : SatelliteEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("satellite")
  private SatelliteEnum satellite = null;

  @JsonProperty("instrument")
  private String instrument = null;

  @JsonProperty("sensorMode")
  private String sensorMode = null;

  @JsonProperty("productType")
  private String productType = null;

  @JsonProperty("productLevel")
  private String productLevel = null;

  @JsonProperty("maximumCloudCoverage")
  private Float maximumCloudCoverage = null;

  public CopernicusSubsetDefinition satellite(SatelliteEnum satellite) {
    this.satellite = satellite;
    return this;
  }

  /**
   * Get satellite
   * @return satellite
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public SatelliteEnum getSatellite() {
    return satellite;
  }

  public void setSatellite(SatelliteEnum satellite) {
    this.satellite = satellite;
  }

  public CopernicusSubsetDefinition instrument(String instrument) {
    this.instrument = instrument;
    return this;
  }

  /**
   * abbreviation for the instrument that is carried by a Sentinel mission [SAR (Sentinel-1 Synthetic Aperture Radar), MSI ( Sentinel-2 MultiSpectral Instrument)] 
   * @return instrument
  **/
  @ApiModelProperty(value = "abbreviation for the instrument that is carried by a Sentinel mission [SAR (Sentinel-1 Synthetic Aperture Radar), MSI ( Sentinel-2 MultiSpectral Instrument)] ")


  public String getInstrument() {
    return instrument;
  }

  public void setInstrument(String instrument) {
    this.instrument = instrument;
  }

  public CopernicusSubsetDefinition sensorMode(String sensorMode) {
    this.sensorMode = sensorMode;
    return this;
  }

  /**
   * abbreviation for sensor mode used by Sentinel-1 satellite instruments [EW (Extra Wide), IW (Interferometric Wide), SM (Stripmap), WV (Wave)] 
   * @return sensorMode
  **/
  @ApiModelProperty(value = "abbreviation for sensor mode used by Sentinel-1 satellite instruments [EW (Extra Wide), IW (Interferometric Wide), SM (Stripmap), WV (Wave)] ")


  public String getSensorMode() {
    return sensorMode;
  }

  public void setSensorMode(String sensorMode) {
    this.sensorMode = sensorMode;
  }

  public CopernicusSubsetDefinition productType(String productType) {
    this.productType = productType;
    return this;
  }

  /**
   * abbreviation for the product type of the Copernicus subset  [RAW (raw data), GRD (Ground Range Detected), SLC (Single Look Complex), OCN (Ocean), L1C (Sentinel-2 Level 1C), L2A (Sentinel-2 Level 2A)] 
   * @return productType
  **/
  @ApiModelProperty(value = "abbreviation for the product type of the Copernicus subset  [RAW (raw data), GRD (Ground Range Detected), SLC (Single Look Complex), OCN (Ocean), L1C (Sentinel-2 Level 1C), L2A (Sentinel-2 Level 2A)] ")


  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public CopernicusSubsetDefinition productLevel(String productLevel) {
    this.productLevel = productLevel;
    return this;
  }

  /**
   * abbreviation for the level of the Copernicus product (e.g) [LEVEL0, LEVEL1, LEVEL2, LEVEL1C, LEVEL2A] 
   * @return productLevel
  **/
  @ApiModelProperty(value = "abbreviation for the level of the Copernicus product (e.g) [LEVEL0, LEVEL1, LEVEL2, LEVEL1C, LEVEL2A] ")


  public String getProductLevel() {
    return productLevel;
  }

  public void setProductLevel(String productLevel) {
    this.productLevel = productLevel;
  }

  public CopernicusSubsetDefinition maximumCloudCoverage(Float maximumCloudCoverage) {
    this.maximumCloudCoverage = maximumCloudCoverage;
    return this;
  }

  /**
   * Get maximumCloudCoverage
   * @return maximumCloudCoverage
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull


  public Float getMaximumCloudCoverage() {
    return maximumCloudCoverage;
  }

  public void setMaximumCloudCoverage(Float maximumCloudCoverage) {
    this.maximumCloudCoverage = maximumCloudCoverage;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CopernicusSubsetDefinition copernicusSubsetDefinition = (CopernicusSubsetDefinition) o;
    return Objects.equals(this.satellite, copernicusSubsetDefinition.satellite) &&
        Objects.equals(this.instrument, copernicusSubsetDefinition.instrument) &&
        Objects.equals(this.sensorMode, copernicusSubsetDefinition.sensorMode) &&
        Objects.equals(this.productType, copernicusSubsetDefinition.productType) &&
        Objects.equals(this.productLevel, copernicusSubsetDefinition.productLevel) &&
        Objects.equals(this.maximumCloudCoverage, copernicusSubsetDefinition.maximumCloudCoverage) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(satellite, instrument, sensorMode, productType, productLevel, maximumCloudCoverage, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CopernicusSubsetDefinition {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    satellite: ").append(toIndentedString(satellite)).append("\n");
    sb.append("    instrument: ").append(toIndentedString(instrument)).append("\n");
    sb.append("    sensorMode: ").append(toIndentedString(sensorMode)).append("\n");
    sb.append("    productType: ").append(toIndentedString(productType)).append("\n");
    sb.append("    productLevel: ").append(toIndentedString(productLevel)).append("\n");
    sb.append("    maximumCloudCoverage: ").append(toIndentedString(maximumCloudCoverage)).append("\n");
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

