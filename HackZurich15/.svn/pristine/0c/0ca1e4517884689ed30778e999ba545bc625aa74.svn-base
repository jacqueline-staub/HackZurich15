using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace PhoneWars.Api.Controllers
{
    public class MapController : Controller
    {
        public ActionResult Index(double lat, double lng, string playerId)
        {
            ViewBag.Lat = lat;
            ViewBag.Lng = lng;
            ViewBag.PlayerId = playerId;

            return View();
        }
    }
}
