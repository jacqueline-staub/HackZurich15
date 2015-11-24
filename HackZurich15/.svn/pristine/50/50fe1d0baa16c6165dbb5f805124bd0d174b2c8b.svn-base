using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace PhoneWars.Api.Models
{
    public class UpdateLocationRequest
    {
        public string PlayerId { get; set; }

        public double Lat { get; set; }

        public double Lng { get; set; }
    }

    public class UpdateLocationResponse
    {
        public PlayerLocation HunterLocation { get; set; }

        public PlayerLocation VictimLocation { get; set; }

        public bool IsDead { get; set; }

        public int ErrorCode { get; set; }

        public string ErrorMessage { get; set; }
    }
}