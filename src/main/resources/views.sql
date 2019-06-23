create or replace view gain_summary as 
select d.name, g.date, g.gain, 
	lc.code as left_code, lc.price as left_price, g.left_nom, 
    rc.code as right_code, rc.price as right_price, g.right_nom
						from path_data d 
						left join path_data_gains pg on pg.path_data_id=d.id 
                        left join gain g on g.id=pg.gains_id
                        left join currency lc on lc.id=g.left_id
                        left join currency rc on rc.id=g.right_id;


create or replace view top_coins as 
	select *, RANK() OVER (PARTITION BY name ORDER BY final_rank DESC) as top_rnk
		from (select name, left_code, (sum(10-rnk)) final_rank, avg(gain) as gain 
			from (select *, RANK() OVER (PARTITION BY date, name ORDER BY gain DESC) as rnk 
					from gain_summary                
					order by name, gain desc, rnk
			) as a
			where a.rnk <= 10
			group by name, left_code
		) as b;
        