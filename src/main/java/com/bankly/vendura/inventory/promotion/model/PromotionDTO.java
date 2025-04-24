package com.bankly.vendura.inventory.promotion.model;

import com.bankly.vendura.utilities.ValidationGroup;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDTO {

  @Null(
      message = "ID will be auto-generated on creation and cannot be updated",
      groups = {ValidationGroup.Create.class, ValidationGroup.Update.class})
  private String id;

  @NotNull(
      message = "Product ID cannot be null",
      groups = {ValidationGroup.Create.class})
  private String productId;

  @NotNull(
      message = "Begin date cannot be null",
      groups = {ValidationGroup.Create.class})
  private Date begin;

  @NotNull(
      message = "End date cannot be null",
      groups = {ValidationGroup.Create.class})
  @Future
  private Date end;

  @NotNull(
      message = "Discount cannot be null",
      groups = {ValidationGroup.Create.class})
  private Double discount;

  @Null(
      message =
          "This field has informative meaning and states whether the current "
              + "date is between begin and end of the promotion")
  private Boolean active;
}
