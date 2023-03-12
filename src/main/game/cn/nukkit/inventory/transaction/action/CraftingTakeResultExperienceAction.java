/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import lombok.ToString;

/**
 * @author joserobjr
 * @since 2020-09-13
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
@ToString(callSuper = true)
public class CraftingTakeResultExperienceAction extends CraftingTakeResultAction {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected int experience;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public CraftingTakeResultExperienceAction(Item sourceItem, Item targetItem, int experience) {
        super(sourceItem, targetItem);
        this.experience = experience;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getExperience() {
        return experience;
    }

    @Override
    public boolean execute(Player source) {
        if (super.execute(source)) {
            int exp = getExperience();
            if (exp > 0) {
                source.getLevel().dropExpOrb(source, exp, null, 3);
            }
            return true;
        }
        return false;
    }
}
