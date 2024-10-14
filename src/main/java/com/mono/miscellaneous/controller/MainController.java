package com.mono.miscellaneous.controller;

import com.mono.miscellaneous.common.controllers.BaseMiscellaneousController;
import com.mono.miscellaneous.common.utilities.CommonEnum;
import com.mono.miscellaneous.payload.ErrorObject;
import com.mono.miscellaneous.payload.IpLookupRequest;
import com.mono.miscellaneous.payload.IpLookupResponse;
import com.mono.miscellaneous.payload.RealtimeWeatherResponse;
import com.mono.miscellaneous.service.RapidApiService;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/miscellaneous/v1")
public class MainController extends BaseMiscellaneousController {
    private RapidApiService rapidApiService;

    public MainController(RapidApiService rapidApiService) {
        this.rapidApiService = rapidApiService;
    }

    @GetMapping("/checkHealth")
    public String healthCheck(){
        return "Responding !!";
    }

    @GetMapping("/checkWeatherReport")
    public ResponseEntity getWeatherReport(
            @RequestParam("lat") float lat,
            @RequestParam("lon") float lon
    ){
        JSONObject reqObj = new JSONObject();
        reqObj.put("lat",lat);
        reqObj.put("lon",lon);

        logRequest(reqObj);
        ResponseEntity response;
        try{
            RealtimeWeatherResponse objResponse = rapidApiService.getRealTimeWeather(lat, lon, getServiceID());
            if (objResponse != null){
                response = ResponseEntity.ok(objResponse);
            }else {
                response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorObject(getServiceID(), CommonEnum.ResponseCode.NOT_FOUND.getCode(), CommonEnum.ResponseCode.NOT_FOUND.getMessage(),getTimeStamp()));
            }
            logResponse(response.getBody());
            return response;
        }catch (Exception e){
            return handleException(e);
        }
    }

    @PostMapping("/getIpLookUp")
    public ResponseEntity getIpLookUp(
            @RequestBody IpLookupRequest ipLookupRequest,
            @RequestHeader("apiKey") String apiKey,
            @RequestHeader("apiHost") String apiHost
    )
    {
        JSONObject reqObj = new JSONObject();
        reqObj.put("ip",ipLookupRequest.getIp());
        reqObj.put("apiKey",apiKey);
        reqObj.put("apiHost",apiHost);

        logRequest(reqObj);
        ResponseEntity response;
        try{
            IpLookupResponse objResponse = rapidApiService.getIplookup(ipLookupRequest,apiKey,apiHost,getServiceID());
            if (objResponse != null){
                response = ResponseEntity.ok(objResponse);
            }else {
                response = ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorObject(getServiceID(), CommonEnum.ResponseCode.NOT_FOUND.getCode(), CommonEnum.ResponseCode.NOT_FOUND.getMessage(),getTimeStamp()));
            }
            logResponse(response.getBody());
            return response;
        }catch (Exception e){
            return handleException(e);
        }
    }
}
