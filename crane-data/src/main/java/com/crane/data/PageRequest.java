package com.crane.data;

public class PageRequest {
  private final int page;
  private final int size;
  private final String sortBy;
  private final boolean ascending;

  public PageRequest(int page, int size) {
    this(page, size, null, true);
  }

  public PageRequest(int page, int size, String sortBy, boolean ascending) {
    if (page < 0) throw new IllegalArgumentException("Page must be >= 0");
    if (size <= 0) throw new IllegalArgumentException("Size must be > 0");

    this.page = page;
    this.size = size;
    this.sortBy = sortBy;
    this.ascending = ascending;
  }

  public int getOffset() { return page * size; }
  public int getPage() { return page; }
  public int getSize() { return size; }
  public String getSortBy() { return sortBy; }
  public boolean isAscending() { return ascending; }
}
