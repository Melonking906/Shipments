package me.nonit.shipments;

import org.bukkit.Material;

public class ShipmentItem
{
  private final Material mat;
  private final Short data;

  public ShipmentItem( Material mat, Short data )
  {
    this.mat = mat;
    this.data = data;
  }

  public Material getMat() { return mat; }

  public Short getData() { return data; }
}