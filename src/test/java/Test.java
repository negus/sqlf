import net.whiteants.util.DDLFormatter;
import net.whiteants.util.SQLFormatter;

public class Test {
    public static void main(String[] args) {

        SQLFormatter formatter = new SQLFormatter(
                "CREATE OR REPLACE FUNCTION ldms.node_upd() RETURNS TRIGGER AS $body$\n" +
                        " DECLARE\n" +
                        " oldPath text;\n" +
                        " newPath text;\n" +
                        " BEGIN\n" +
                        " -- Prevent share update\n" +
                        " IF OLD.share_id <> NEW.share_id THEN\n" +
                        " RAISE EXCEPTION 'Cannot change the share of existing node.';\n" +
                        " END IF;\n" +
                        " -- Fetch old path\n" +
                        " SELECT ns.path INTO oldPath\n" +
                        " FROM ldms.nodestate ns\n" +
                        " WHERE ns.id = NEW.id;\n" +
                        " -- Determine new path\n" +
                        " IF (NEW.parent_id IS NULL) THEN\n" +
                        " SELECT '/' || NEW.name INTO newPath;\n" +
                        " ELSE\n" +
                        " SELECT ns.path || '/' || NEW.name INTO newPath\n" +
                        " FROM ldms.nodestate ns\n" +
                        " WHERE ns.id = NEW.parent_id;\n" +
                        " END IF;\n" +
                        " -- Update path of node and all it's descendants\n" +
                        " UPDATE ldms.nodestate SET path = newPath WHERE id = NEW.id;\n" +
                        " UPDATE ldms.nodestate\n" +
                        " SET path = newPath || (regexp_matches(path, oldPath || '(/.*)')::text[])[1]\n" +
                        " WHERE id IN (SELECT n.id FROM ldms.node n WHERE n.share_id = NEW.share_id);\n" +
                        " -- Evaluate node state\n" +
                        " PERFORM ldms.eval_node_state(NEW.id);\n" +
                        " -- If parent has changed (node has been moved)\n" +
                        " -- evaluate old parent state too.\n" +
                        " IF (OLD.parent_id <> NEW.parent_id) THEN\n" +
                        " PERFORM ldms.eval_node_state(OLD.parent_id);\n" +
                        " END IF;\n" +
                        " RETURN NEW;\n" +
                        " END\n" +
                        " $body$ language 'plpgsql';");
        System.out.println(formatter.format());
        System.out.println(new DDLFormatter().format("CREATE TABLE abc (id bigint not null, col varchar(10) not null unique references foo(col) on delete cascade);"));
    }}





/*      DDLFormatter form = new DDLFormatter();
   System.out.println(form.format(
                    "CREATE OR REPLACE FUNCTION ldms.node_upd\n" +
                            "        () RETURNS TRIGGER\n" +
                            "AS\n" +
                            "        $body$\n" +
                            "        DECLARE\n" +
                            "                oldPath text;\n" +
                            "                newPath text;\n" +
                            "        BEGIN\n" +
                            "                -- Prevent share update\n" +
                            "                IF OLD.share_id <> NEW.share_id THEN\n" +
                            "                        RAISE\n" +
                            "                EXCEPTION\n" +
                            "                        'Cannot change the share of existing node.';\n" +
                            "                END IF;\n" +
                            "                -- Fetch old path\n" +
                            "                SELECT ns.path\n" +
                            "                INTO   oldPath\n" +
                            "                FROM   ldms.nodestate ns\n" +
                            "                WHERE  ns.id = NEW.id;\n" +
                            "                \n" +
                            "                -- Determine new path\n" +
                            "                IF (NEW.parent_id IS NULL) THEN\n" +
                            "                        SELECT '/'\n" +
                            "                                      || NEW.name\n" +
                            "                        INTO   newPath;\n" +
                            "                \n" +
                            "                ELSE\n" +
                            "                        SELECT ns.path\n" +
                            "                                      || '/'\n" +
                            "                                      || NEW.name\n" +
                            "                        INTO   newPath\n" +
                            "                        FROM   ldms.nodestate ns\n" +
                            "                        WHERE  ns.id = NEW.parent_id;\n" +
                            "                \n" +
                            "                END IF;\n" +
                            "                -- Update path of node and all it's descendants\n" +
                            "                UPDATE ldms.nodestate\n" +
                            "                SET    path = newPath\n" +
                            "                WHERE  id   = NEW.id;\n" +
                            "                \n" +
                            "                UPDATE ldms.nodestate\n" +
                            "                SET    path = newPath\n" +
                            "                              || (regexp_matches(path, oldPath\n" +
                            "                              || '(/.*)')::text[])[1]\n" +
                            "                WHERE  id IN\n" +
                            "                       (SELECT n.id\n" +
                            "                       FROM    ldms.node n\n" +
                            "                       WHERE   n.share_id = NEW.share_id\n" +
                            "                       );\n" +
                            "                \n" +
                            "                -- Evaluate node state\n" +
                            "                PERFORM ldms.eval_node_state(NEW.id);\n" +
                            "                -- If parent has changed (node has been moved)\n" +
                            "                -- evaluate old parent state too.\n" +
                            "                IF (OLD.parent_id <> NEW.parent_id) THEN\n" +
                            "                        PERFORM ldms.eval_node_state(OLD.parent_id);\n" +
                            "                END IF;\n" +
                            "                RETURN NEW;\n" +
                            "        END $body$ language 'plpgsql';"));

}*/

/*	public static void main(String[] args) {
		System.out.println(
			new SQLFormatter("insert  into Address (city, state, zip, \"from\") values (?, ?, ?, 'insert value')").format()
		);
		System.out.println(
			new SQLFormatter("delete from Address where id = ? and version = ?").format()
		);
		System.out.println(
			new SQLFormatter("update Address set city = ?, state=?, zip=?, version = ? where id = ? and version = ?").format()
		);
		System.out.println(
			new SQLFormatter("update Address set city = ?, state=?, zip=?, version = ? where id in (select aid from Person)").format()
		);
		System.out.println(
			new SQLFormatter("select p.name, a.zipCode, count(*) from Person p left outer join Employee e on e.id = p.id and p.type = 'E' and (e.effective>? or e.effective<?) join Address a on a.pid = p.id where upper(p.name) like 'G%' and p.age > 100 and (p.sex = 'M' or p.sex = 'F') and coalesce( trim(a.street), a.city, (a.zip) ) is not null order by p.name asc, a.zipCode asc").format()
		);
		System.out.println(
			new SQLFormatter("select ( (m.age - p.age) * 12 ), trim(upper(p.name)) from Person p, Person m where p.mother = m.id and ( p.age = (select max(p0.age) from Person p0 where (p0.mother=m.id)) and p.name like ? )").format()
		);
		System.out.println(
			new SQLFormatter("select * from Address a join Person p on a.pid = p.id, Person m join Address b on b.pid = m.id where p.mother = m.id and p.name like ?").format()
		);
		System.out.println(
			new SQLFormatter("select case when p.age > 50 then 'old' when p.age > 18 then 'adult' else 'child' end from Person p where ( case when p.age > 50 then 'old' when p.age > 18 then 'adult' else 'child' end ) like ?").format()
		);
	}*/
