package me.nonit.shipments;

import org.bukkit.Material;

public class ShipmentItem
{
  private final Material material;
  private final Short data;
  private double value;

  ShipmentItem( Material material, Short data, double value )
  {
    this.material = material;
    this.data = data;
    this.value = value;
  }

  public Material getType() { return material; }

  public Short getData() { return data; }

  public double getValue()
  {
    return value;
  }
}