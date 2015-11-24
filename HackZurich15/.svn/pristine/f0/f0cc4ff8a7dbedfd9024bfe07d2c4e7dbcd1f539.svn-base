using MongoRepository;
using PhoneWars.Api.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace PhoneWars.Api.Controllers
{
    public class HomeController : Controller
    {
        private MongoRepository<Player> _playerRepo;

        public HomeController()
        {
            _playerRepo = new MongoRepository<Player>();
        }

        public ActionResult Index()
        {
            MainModel model = new MainModel();
            model.Stats = new List<PlayerStats>();

            var players = _playerRepo.ToList();
          
            foreach (var player in players)
            {
                PlayerStats stats = new PlayerStats();

                stats.ImageUrl = "/api/getimage?playerId=" + player.Id;
                stats.Nickname = player.Nickname;
                stats.Level = player.Level;
                stats.HunterName = player.HunterId != null ? players.First(x => x.Id == player.HunterId).Nickname : null;
                stats.VictimName = player.VictimId != null ? players.First(x => x.Id == player.VictimId).Nickname : null;

                model.Stats.Add(stats);
            }

            return View(model);
        }
    }

    public class MainModel
    {
        public List<PlayerStats> Stats { get; set; }

    }

    public class PlayerStats
    {
        public string ImageUrl { get; set; }

        public string Nickname { get; set; }

        public int Level { get; set; }

        public string HunterName { get; set; }

        public string VictimName { get; set; }

    }
}
