using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace PhoneWars.Api.Models
{
    public class ResetDeadRequest
    {
        public string PlayerId { get; set; }

    }

    public class ResetDeadResponse
    {
        public int ErrorCode { get; set; }

        public string ErrorMessage { get; set; }
    }
}