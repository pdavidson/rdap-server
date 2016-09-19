#storeToDatabase
INSERT INTO rdap.event VALUES(null,?,?,str_to_date(?,'%m/%d/%Y %H:%i:%s');

#getByNameServerId
SELECT eve_id,eve_action,eve_actor,FROM_UNIXTIME(eve_date,'%m/%d/%Y %H:%i:%s') as eve_date FROM rdap.event eve JOIN rdap.nameserver_events nse ON nse.eve_id=eve.eve_id WHERE nse.nse_id=?;

#storeNameserverEventsToDatabase
INSERT INTO rdap.nameserver_events values (?,?);