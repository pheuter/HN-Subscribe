package storm.hnsubscribe;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import yieldbot.storm.spout.RedisPubSubSpout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Map;


public class PostTopology {
  public static class HomepageSpout extends BaseRichSpout {
    SpoutOutputCollector _collector;

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
      _collector = collector;
    }

    @Override
    public void nextTuple() {
      Document doc = null;
      
      try {
        doc = Jsoup.connect("http://news.ycombinator.com").get();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
            
      if (doc != null) {
        Elements elements = doc.select("td.title a");
        
        for (Element el : elements) {
          _collector.emit(new Values(el.attr("href")));
        }
      }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("links"));
      
    }
  }

  public static class ParsePostBolt extends BaseRichBolt {
    OutputCollector _collector;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
      _collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
      Document doc = null;
      
      try {
        doc = Jsoup.connect(tuple.getString(0)).get();
  	  } catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
  	  }
            
      if (doc != null) {
        Element title = doc.select("td.title a").first();
          
        if (title != null)
        	_collector.emit(tuple, new Values(title.text()));
        
        _collector.ack(tuple);
      }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("title"));
    }


  }

  public static void main(String[] args) throws Exception {
    TopologyBuilder builder = new TopologyBuilder();

    // builder.setSpout("post", new RedisPubSubSpout("127.0.0.1", 6379, "post"));
    builder.setSpout("link", new HomepageSpout());
    builder.setBolt("parse", new ParsePostBolt(), 1).shuffleGrouping("link");

    Config conf = new Config();
    conf.setDebug(true);

    if (args != null && args.length > 0) {
      conf.setNumWorkers(3);

      StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
    }
    else {
      LocalCluster cluster = new LocalCluster();
      cluster.submitTopology("test", conf, builder.createTopology());
      // Utils.sleep(10000);
      // cluster.killTopology("test");
      // cluster.shutdown();
    }
  }
}
