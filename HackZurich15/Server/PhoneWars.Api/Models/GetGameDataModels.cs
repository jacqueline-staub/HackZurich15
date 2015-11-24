using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace PhoneWars.Api.Models
{
    public class GetGameDataRequest
    {
        public string PlayerId { get; set; }
    }

    public class GetGameDataResponse
    {
        public Player Player { get; set; }

        public Player Victim { get; set; }

        public Player Hunter { get; set; }

        public int ErrorCode { get; set; }

        public string ErrorMessage { get; set; }
    }
}