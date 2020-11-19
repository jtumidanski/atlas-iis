package com.atlas.iis.model;

public record Equipment(int itemId, int strength, int dexterity, int intelligence, int luck, int weaponAttack, int weaponDefense,
                        int magicAttack, int magicDefense, int accuracy, int avoidability, int speed, int jump, int hp, int mp,
                        int slots) {
}
