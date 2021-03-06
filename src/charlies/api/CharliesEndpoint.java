package charlies.api;

import java.util.ArrayList;
import java.util.List;

import charlies.datastore.DatastoreManager;
import charlies.entities.Athlete;
import charlies.entities.Battle;
import charlies.entities.Highscores;
import charlies.entities.Scientist;
import charlies.entities.Score;
import charlies.exceptions.NoResultException;
import charlies.exceptions.UnknownCategoryException;
import charlies.generators.GeneratorManager;
import charlies.generators.SparqlService;
import charlies.sections.QuizzSection;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.DefaultValue;
import com.google.api.server.spi.config.Named;

@Api(name = "charlies", version="v1")
public class CharliesEndpoint {

	private DatastoreManager manager = new DatastoreManager();
	private GeneratorManager generator = new GeneratorManager(manager);
	private SparqlService sparql = new SparqlService();
	
	@ApiMethod(path="/highscores")
	public Highscores getHighscores(@Named("category") @DefaultValue("") String category, @Named("player") @DefaultValue("") String player) {
		
		List<Score> highscores = new ArrayList<Score>();
		Score scorePlayer;
		int place;
		
		if (category.equals("")){
			highscores = manager.listHighscores();
			place = manager.getBetterPlace(player);
		} else {
			highscores = manager.listHighscores(category);
			place = manager.getBetterPlace(player, category);
		}
		scorePlayer = manager.getBetterScore(player);
		
		return new Highscores(highscores,place,scorePlayer);
	}
		
	@ApiMethod(path="/questions")
	public List<QuizzSection> listQuestions(@Named("category") @DefaultValue("") String category, @Named("number") @DefaultValue("3") int number, @Named("nbAnswsers") @DefaultValue("3") int nbAnswers) throws UnknownCategoryException, NoResultException{
		List<QuizzSection> sections = generator.generate(number,category);
		return sections;
	}
	
	@ApiMethod(path="/addscore")
	public void insertScore(@Named("category") String category, @Named("pic") String pic, @Named("name") String name, @Named("score") int score){
		Score s = new Score(category, name, pic, score);
		manager.insertScore(s);
	}
	
	@ApiMethod(path="/categories")
	public List<String> listCategories(){
		return generator.getCategories();
	}

	/*
	 * Uncomment this method to fill the datastore
	@ApiMethod(path="/fill")
	public void insertQuestions(@Named("category") String category) throws Exception{
		try {
			switch(category){
			case "scientists":
				List<Scientist> s = generator.generateScientists(sparql.getScientists());
				manager.fillScientists(s);
			case "battles":
				List<Battle> b = generator.generateBattles(sparql.getBattles());
				manager.fillBattles(b);
			case "athletes":
				List<Athlete> a = generator.generateAthletes(sparql.getAthletes());
				manager.fillAthletes(a);
			default:
				throw new UnknownCategoryException();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

}
