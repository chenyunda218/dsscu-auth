package mo.gov.dsscu.auth.controller.models;

import lombok.Data;

@Data
public class Pagination<T> {
  private int page;
  private int size;
  private long total;
  private T[] items;
}
