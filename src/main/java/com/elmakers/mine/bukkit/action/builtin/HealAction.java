package com.elmakers.mine.bukkit.action.builtin;

import com.elmakers.mine.bukkit.api.action.CastContext;
import com.elmakers.mine.bukkit.api.magic.Mage;
import com.elmakers.mine.bukkit.api.spell.Spell;
import com.elmakers.mine.bukkit.api.spell.SpellResult;
import com.elmakers.mine.bukkit.spell.BaseSpell;
import com.elmakers.mine.bukkit.action.BaseSpellAction;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import java.util.Arrays;
import java.util.Collection;

public class HealAction extends BaseSpellAction
{
    private double percentage;
    private double amount;

    @Override
    public void prepare(CastContext context, ConfigurationSection parameters)
    {
        super.prepare(context, parameters);
        percentage = parameters.getDouble("percentage", 0);
        amount = parameters.getDouble("amount", 20);
    }

	@Override
	public SpellResult perform(CastContext context)
	{
        Entity entity = context.getTargetEntity();
		if (!(entity instanceof LivingEntity))
		{
			return SpellResult.NO_TARGET;
		}

        LivingEntity targetEntity = (LivingEntity)entity;
        if (targetEntity.getHealth() == targetEntity.getMaxHealth())
        {
            return SpellResult.NO_TARGET;
        }

        double healAmount = amount;
        Mage mage = context.getMage();
        if (percentage > 0)
        {
            healAmount = targetEntity.getMaxHealth() * percentage;
        }
        else
        {
            healAmount *= mage.getDamageMultiplier();
        }

        EntityRegainHealthEvent event = new EntityRegainHealthEvent(targetEntity, healAmount, RegainReason.CUSTOM);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return SpellResult.CANCELLED;
        }
        healAmount = event.getAmount();
        if (healAmount == 0)
        {
            return SpellResult.NO_TARGET;
        }

        context.registerModified(targetEntity);
        targetEntity.setHealth(Math.min(targetEntity.getHealth() + healAmount, targetEntity.getMaxHealth()));

		return SpellResult.CAST;
	}

    @Override
    public boolean isUndoable()
    {
        return true;
    }

    @Override
    public boolean requiresTargetEntity()
    {
        return true;
    }

    @Override
    public void getParameterNames(Spell spell, Collection<String> parameters) {
        super.getParameterNames(spell, parameters);
        parameters.add("percentage");
        parameters.add("amount");
    }

    @Override
    public void getParameterOptions(Spell spell, String parameterKey, Collection<String> examples) {
        if (parameterKey.equals("percentage")) {
            examples.addAll(Arrays.asList((BaseSpell.EXAMPLE_PERCENTAGES)));
        } else if (parameterKey.equals("amount")) {
            examples.addAll(Arrays.asList((BaseSpell.EXAMPLE_SIZES)));
        } else {
            super.getParameterOptions(spell, parameterKey, examples);
        }
    }
}
