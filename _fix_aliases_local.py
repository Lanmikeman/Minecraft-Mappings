#!/usr/bin/env python3
"""Add high-frequency yarn aliases for local MTR smoke."""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parent
MAPPING_SRC = ROOT / "fabric" / "26.1.2-mapping" / "src" / "main" / "java" / "org" / "mtr" / "mapping"
HOLDER = MAPPING_SRC / "holder"
MAPPER = MAPPING_SRC / "mapper"


def append_force(path: Path, snip: str, needle: str) -> None:
    t = path.read_text(encoding="utf-8").rstrip()
    if needle in t:
        print(path.name, "has", needle)
        return
    if t.count("{") != t.count("}"):
        raise SystemExit(f"unbalanced {path}")
    path.write_text(t[:-1] + snip + "}", encoding="utf-8")
    t2 = path.read_text(encoding="utf-8")
    if t2.count("{") != t2.count("}"):
        raise SystemExit(f"unbalanced after {path}")
    print(path.name, "added", needle)


# BooleanProperty.of
append_force(
    HOLDER / "BooleanProperty.java",
    "@Nonnull@MappedMethod public static BooleanProperty of(java.lang.String name){return create(name);}",
    " of(java.lang.String",
)

# EnumProperty.of
ep = (HOLDER / "EnumProperty.java").read_text(encoding="utf-8")
print("EnumProperty create?", "create(" in ep)
# MC: EnumProperty.create(String, Class)
append_force(
    HOLDER / "EnumProperty.java",
    "@Nonnull@MappedMethod public static <T extends java.lang.Enum<T>&net.minecraft.util.StringRepresentable> EnumProperty of(java.lang.String name,java.lang.Class<T> clazz){return new EnumProperty(net.minecraft.world.level.block.state.properties.EnumProperty.create(name,clazz));}"
    "@Nonnull@MappedMethod public static <T extends java.lang.Enum<T>&net.minecraft.util.StringRepresentable> EnumProperty of(java.lang.String name,java.lang.Class<T> clazz,java.util.function.Predicate<T> filter){return new EnumProperty(net.minecraft.world.level.block.state.properties.EnumProperty.create(name,clazz,filter));}",
    " of(java.lang.String name,java.lang.Class",
)

# BlockSettings.nonOpaque
bs = (HOLDER / "BlockSettings.java").read_text(encoding="utf-8")
print("BlockSettings noOcclusion", "noOcclusion" in bs, "nonOpaque" in bs)
# find wrapper method names
print("methods", re.findall(r"MappedMethod public[^{]+\{", bs)[:8])
append_force(
    HOLDER / "BlockSettings.java",
    "@Nonnull@MappedMethod public BlockSettings nonOpaque(){this.data.noOcclusion();return this;}",
    "nonOpaque()",
)

# BlockState isOf / with
bst = (HOLDER / "BlockState.java").read_text(encoding="utf-8")
print("BlockState is(", "is(" in bst, "setValue" in bst, "with(" in bst)
# typically holders use setValue mapped; yarn with -> setValue
append_force(
    HOLDER / "BlockState.java",
    "@MappedMethod public boolean isOf(Block block){return this.data.is(block.data);}"
    "@Nonnull@MappedMethod public <T extends java.lang.Comparable<T>> BlockState with(Property<T> property,T value){return new BlockState(this.data.setValue(property.data,value));}"
    "@Nonnull@MappedMethod public BlockState with(BooleanProperty property,boolean value){return new BlockState(this.data.setValue(property.data,value));}",
    "isOf(Block",
)

# BlockPos up
bp = (HOLDER / "BlockPos.java").read_text(encoding="utf-8")
print("BlockPos up", "up(" in bp, "above(" in bp)
append_force(
    HOLDER / "BlockPos.java",
    "@Nonnull@MappedMethod public BlockPos up(){return new BlockPos(this.data.above());}"
    "@Nonnull@MappedMethod public BlockPos up(int n){return new BlockPos(this.data.above(n));}"
    "@Nonnull@MappedMethod public BlockPos down(){return new BlockPos(this.data.below());}"
    "@Nonnull@MappedMethod public BlockPos down(int n){return new BlockPos(this.data.below(n));}",
    " up()",
)

# Block getDefaultState2
b = (HOLDER / "Block.java").read_text(encoding="utf-8")
print("Block getDefaultState", "getDefaultState" in b, "defaultBlockState" in b)
append_force(
    HOLDER / "Block.java",
    "@Nonnull@MappedMethod public BlockState getDefaultState2(){return new BlockState(this.data.defaultBlockState());}",
    "getDefaultState2()",
)

# BlockEntityExtension world/state
ext = (MAPPER / "BlockEntityExtension.java").read_text(encoding="utf-8")
if "getWorld2" not in ext:
    snip = """
	@MappedMethod
	public World getWorld2() {
		final net.minecraft.world.level.Level level = getLevel();
		return level == null ? null : new World(level);
	}

	@MappedMethod
	public BlockState getCachedState2() {
		return new BlockState(getBlockState());
	}
"""
    (MAPPER / "BlockEntityExtension.java").write_text(ext.rstrip()[:-1] + snip + "}\n", encoding="utf-8")
    print("BlockEntityExtension world/state")
else:
    print("BlockEntityExtension already has getWorld2")

# IntegerProperty.of if exists
ip = HOLDER / "IntegerProperty.java"
if ip.exists():
    append_force(
        ip,
        "@Nonnull@MappedMethod public static IntegerProperty of(java.lang.String name,int min,int max){return new IntegerProperty(net.minecraft.world.level.block.state.properties.IntegerProperty.create(name,min,max));}",
        " of(java.lang.String name,int",
    )

print("done aliases")
