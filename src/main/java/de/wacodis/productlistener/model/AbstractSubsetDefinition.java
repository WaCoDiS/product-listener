package de.wacodis.productlistener.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * AbstractSubsetDefinition
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2019-05-21T12:49:26.085+02:00[Europe/Berlin]")

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "sourceType", visible = true)
@JsonSubTypes({
  @JsonSubTypes.Type(value = SensorWebSubsetDefinition.class, name = "SensorWebSubsetDefinition"),
  @JsonSubTypes.Type(value = CopernicusSubsetDefinition.class, name = "CopernicusSubsetDefinition"),
  @JsonSubTypes.Type(value = CatalogueSubsetDefinition.class, name = "CatalogueSubsetDefinition"),
  @JsonSubTypes.Type(value = DwdSubsetDefinition.class, name = "DwdSubsetDefinition"),
})

public class AbstractSubsetDefinition  implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * shall be used to determine the responsible data backend 
   */
  public enum SourceTypeEnum {
    SENSORWEBSUBSETDEFINITION("SensorWebSubsetDefinition"),
    
    COPERNICUSSUBSETDEFINITION("CopernicusSubsetDefinition"),
    
    CATALOGUESUBSETDEFINITION("CatalogueSubsetDefinition"),
    
    DWDSUBSETDEFINITION("DwdSubsetDefinition");

    private String value;

    SourceTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static SourceTypeEnum fromValue(String text) {
      for (SourceTypeEnum b : SourceTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("sourceType")
  private SourceTypeEnum sourceType = null;

  @JsonProperty("identifier")
  private String identifier = null;

  public AbstractSubsetDefinition sourceType(SourceTypeEnum sourceType) {
    this.sourceType = sourceType;
    return this;
  }

  /**
   * shall be used to determine the responsible data backend 
   * @return sourceType
  **/
  @ApiModelProperty(required = true, value = "shall be used to determine the responsible data backend ")
  @NotNull


  public SourceTypeEnum getSourceType() {
    return sourceType;
  }

  public void setSourceType(SourceTypeEnum sourceType) {
    this.sourceType = sourceType;
  }

  public AbstractSubsetDefinition identifier(String identifier) {
    this.identifier = identifier;
    return this;
  }

  /**
   * identifier for matching analysis process inputs 
   * @return identifier
  **/
  @ApiModelProperty(required = true, value = "identifier for matching analysis process inputs ")
  @NotNull


  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractSubsetDefinition abstractSubsetDefinition = (AbstractSubsetDefinition) o;
    return Objects.equals(this.sourceType, abstractSubsetDefinition.sourceType) &&
        Objects.equals(this.identifier, abstractSubsetDefinition.identifier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceType, identifier);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AbstractSubsetDefinition {\n");
    
    sb.append("    sourceType: ").append(toIndentedString(sourceType)).append("\n");
    sb.append("    identifier: ").append(toIndentedString(identifier)).append("\n");
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

