package phase2.Tile;

public class TileResources {
    public static final TileDef[] TILE_DEFS = new TileDef[]{
            new TileDef("0x72_16x16DungeonTileset.v5/items/floor_plain.png"), //0
            new TileDef("0x72_16x16DungeonTileset.v5/items/wall_center.png")
                    .collision(true).spawnable(false), //1
            new TileDef("0x72_16x16DungeonTileset.v5/items/column_wall.png"), //2

            new TileDef("Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(1).png"), //3
            new TileDef("Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(45).png"), //4
            new TileDef("Top_Down_Adventure_Pack_v.1.0/Tiles_(animated)/Overworld/water_tile_anim.gif")
                    .collision(true).spawnable(false), //5
            new TileDef("Top_Down_Adventure_Pack_v.1.0/Tiles_(animated)/Overworld/edge_water_tile_anim_strip_8.png")
                    .collision(true).spawnable(false), //6
            new TileDef("Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(6).png"), //7
            new TileDef("Top_Down_Adventure_Pack_v.1.0/Tiles_(animated)/Overworld/edge_water_tile_anim_strip_(1).png")
                    .collision(true).spawnable(false), //8
            new TileDef("Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(39).png"), //9
            new TileDef("Top_Down_Adventure_Pack_v.1.0/Overworld_individual_tiles/ezgif.com-crop(14).png"), //10

            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_inner_e.png")
                    .collision(true).spawnable(false), //11
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_inner_ne.png")
                    .collision(true).spawnable(false), //12
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_inner_nw.png")
                    .collision(true).spawnable(false), //13
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_inner_se.png")
                    .collision(true).spawnable(false), //14
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_inner_sw.png")
                    .collision(true).spawnable(false), //15
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_inner_w.png")
                    .collision(true).spawnable(false), //16
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_outer_e.png")
                    .collision(true).spawnable(false), //17
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_outer_e2.png")
                    .collision(true).spawnable(false), //18
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_outer_n.png")
                    .collision(true).spawnable(false), //19
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_outer_ne.png")
                    .collision(true).spawnable(false), //20
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_outer_nw.png")
                    .collision(true).spawnable(false), //21
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_outer_se.png")
                    .collision(true).spawnable(false), //22
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_outer_sw.png")
                    .collision(true).spawnable(false), //23
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_outer_w.png")
                    .collision(true).spawnable(false), //24
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_outer_w.png")
                    .collision(true).spawnable(false), //25 duplicate to match original

            new TileDef("tile/blackTile.png").collision(true).spawnable(false), //26
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_outer_n_reversed.png")
                    .collision(true).spawnable(false), //27
            new TileDef("0x72_16x16DungeonTileset.v5/items/chest_golden_closed.png")
                    .collision(true).spawnable(false), //28
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_inner_sw_reversed.png")
                    .collision(true).spawnable(false), //29
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_inner_se_reversed.png")
                    .collision(true).spawnable(false), //30
            new TileDef("0x72_16x16DungeonTileset.v5/items/torch_8.png")
                    .trap(true, 15, 60).spawnable(false), //31
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_outer_ne[reversed].png")
                    .collision(true).spawnable(false), //32
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_outer_ne[reversed2].png")
                    .collision(true).spawnable(false), //33
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_outer_ne[reversed3].png")
                    .collision(true).spawnable(false), //33
            new TileDef("0x72_16x16DungeonTileset.v5/items/Wall_outer_ne[reversed4].png")
                    .collision(true).spawnable(false) //34
    };
}
