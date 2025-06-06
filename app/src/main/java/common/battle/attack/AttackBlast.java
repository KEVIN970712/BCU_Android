package common.battle.attack;

import common.battle.entity.AbEntity;
import common.battle.entity.Entity;

import java.util.LinkedList;
import java.util.List;

public class AttackBlast extends AttackAb {

    public final float reduction;
    public int lv = 0;
    public int raw;
    public boolean attacked = false;
    private final LinkedList<AbEntity>[] ents = new LinkedList[] {new LinkedList<AbEntity>(), new LinkedList<AbEntity>()};

    protected AttackBlast(Entity attacker, AttackSimple src, float pos, int bt) {
        super(attacker, src, pos + 75, pos - 75, false);
        waveType = bt;
        reduction = src.getProc().BLAST.reduction;
        raw = ((AtkModelEntity)model).getDefAtk(matk);
    }

    @Override
    public void capture() {
        capt.clear();
        float rng = lv == 0 ? 0 : 25 + (100 * lv);

        List<AbEntity> le = model.b.inRange(touch, attacker != null && attacker.status.rage > 0 ? 2 : dire, sta + rng, end + rng, excludeRightEdge);
        le.removeAll(ents[0]);
        ents[0].addAll(le);
        if (lv > 0) {
            List<AbEntity> nle = model.b.inRange(touch, attacker != null && attacker.status.rage > 0 ? 2 : dire, sta - rng, end - rng, excludeRightEdge);
            nle.removeAll(le);
            nle.removeAll(ents[1]);
            ents[1].addAll(nle);
            le.addAll(nle);
        }
        le.remove(dire == 1 ? model.b.ubase : model.b.ebase);
        if (attacker != null && matk.getDire() * dire > 0 && (attacker.status.rage > 0 || attacker.status.hypno > 0))
            le.remove(attacker);
        if ((abi & AB_ONLY) == 0)
            capt.addAll(le);
        else
            for (AbEntity e : le)
                if (e.ctargetable(trait, attacker))
                    capt.add(e);
    }

    @Override
    public void excuse() {
        atk = ((AtkModelEntity)model).getEffMult(raw);
        atk -= (int)(lv * reduction / 100 * atk);
        process();

        for (AbEntity e : capt)
            e.damaged(this);
        attacked = true;
    }

    public void next() {
        ents[0].clear();
        ents[1].clear();
        if (++lv == 1) {
            sta -= 25;
            end += 25;
        }
    }
}
