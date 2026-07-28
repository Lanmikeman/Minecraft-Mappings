#!/usr/bin/env python3
"""One-off yarn→official alias patches for the 26.1.2 mapping smoke port."""
from pathlib import Path

ROOT = Path(__file__).resolve().parent
MAPPING_SRC = ROOT / "fabric" / "26.1.2-mapping" / "src" / "main" / "java" / "org" / "mtr" / "mapping"
HOLDER = MAPPING_SRC / "holder"
MAPPER = MAPPING_SRC / "mapper"

# StringIdentifiable: bridge asString2 <-> getSerializedName2
p = HOLDER / "StringIdentifiable.java"
t = p.read_text(encoding="utf-8")
old = "@Nonnull@MappedMethod  java.lang.String getSerializedName2();@Deprecated  default java.lang.String getSerializedName(){return getSerializedName2();}"
new = (
    "@Nonnull@MappedMethod java.lang.String asString2();"
    "@Nonnull@MappedMethod default java.lang.String getSerializedName2(){return asString2();}"
    "@Deprecated default java.lang.String getSerializedName(){return getSerializedName2();}"
)
if old in t:
    p.write_text(t.replace(old, new), encoding="utf-8")
    print("StringIdentifiable bridged")
elif "asString2" in t:
    print("StringIdentifiable already bridged")
else:
    print("StringIdentifiable pattern mismatch")
    idx = t.find("getSerializedName2")
    print(repr(t[idx - 40 : idx + 120]))

# BlockAbstractMapping aliases
bam = HOLDER / "BlockAbstractMapping.java"
bt = bam.read_text(encoding="utf-8").rstrip()
snip = ""
if "getDefaultState2(" not in bt:
    snip += "@Nonnull@MappedMethod public BlockState getDefaultState2(){return defaultBlockState2();}"
if "setDefaultState2(" not in bt:
    # registerDefaultState in Mojmap
    snip += "@MappedMethod public void setDefaultState2(BlockState state){registerDefaultState(state.data);}"
if snip:
    if bt.count("{") != bt.count("}"):
        raise SystemExit("bam unbalanced")
    bam.write_text(bt[:-1] + snip + "}", encoding="utf-8")
    print("BlockAbstractMapping aliases added")
else:
    print("BlockAbstractMapping already has aliases")

# VoxelShapes.union
vs = HOLDER / "VoxelShapes.java"
vt = vs.read_text(encoding="utf-8")
if "union(" not in vt:
    # Shapes.or
    snip = "@Nonnull@MappedMethod public static VoxelShape union(VoxelShape a,VoxelShape b){return new VoxelShape(net.minecraft.world.phys.shapes.Shapes.or(a.data,b.data));}"
    vs.write_text(vt.rstrip()[:-1] + snip + "}", encoding="utf-8")
    print("VoxelShapes.union added")
else:
    print("VoxelShapes has union")

# ActionResult SUCCESS/FAIL/CONSUME - check if enum or holder
ar = HOLDER / "ActionResult.java"
at = ar.read_text(encoding="utf-8")
print("ActionResult start", at[:300])
print("SUCCESS", "SUCCESS" in at, "success" in at.lower())

# BlockEntity markDirty2
be = MAPPER / "BlockEntityExtension.java"
bet = be.read_text(encoding="utf-8")
if "markDirty2" not in bet:
    be.write_text(
        bet.rstrip()[:-1]
        + "\n\t@MappedMethod\n\tpublic void markDirty2() {\n\t\tsetChanged();\n\t}\n}\n",
        encoding="utf-8",
    )
    print("markDirty2 added")
else:
    print("markDirty2 exists")

# BlockState.get(Property)
bst = HOLDER / "BlockState.java"
bstt = bst.read_text(encoding="utf-8")
if " get(Property<" not in bstt and " get(BooleanProperty" not in bstt:
    # may have get already
    print("BlockState get methods", "get(" in bstt, "getValue" in bstt)
    snip = (
        "@MappedMethod public <T extends java.lang.Comparable<T>> T get(Property<T> property){return this.data.getValue(property.data);}"
        "@MappedMethod public boolean get(BooleanProperty property){return this.data.getValue(property.data);}"
        "@MappedMethod public <T extends java.lang.Comparable<T>> boolean contains(Property<T> property){return this.data.hasProperty(property.data);}"
    )
    if bstt.count("{") == bstt.count("}"):
        bst.write_text(bstt.rstrip()[:-1] + snip + "}", encoding="utf-8")
        print("BlockState get/contains added")
else:
    print("BlockState get already")

print("done")
