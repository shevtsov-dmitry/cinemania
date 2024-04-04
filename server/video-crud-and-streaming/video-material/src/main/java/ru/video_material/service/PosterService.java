package ru.video_material.service;

import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.video_material.model.ContentMetadata;
import ru.video_material.model.Poster;
import ru.video_material.repo.MetadataRepo;
import ru.video_material.repo.PosterRepo;
import ru.video_material.util.PosterWithMetadata;

import java.io.IOException;
import java.util.*;

@Service
public class PosterService {

    private final PosterRepo posterRepo;
    private final MetadataRepo metadataRepo;
    private static final Logger log = LoggerFactory.getLogger(PosterService.class);
    private static final String defaultNotFoundImage = "/9j/4AAQSkZJRgABAQAAAQABAAD/4QAqRXhpZgAASUkqAAgAAAABADEBAgAHAAAAGgAAAAAAAABQaWNhc2EAAP/iC/hJQ0NfUFJPRklMRQABAQAAC+gAAAAAAgAAAG1udHJSR0IgWFlaIAfZAAMAGwAVACQAH2Fjc3AAAAAAAAAAAAAAAAAAAAAAAAAAAQAAAAAAAAAAAAD21gABAAAAANMtAAAAACn4Pd6v8lWueEL65MqDOQ0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEGRlc2MAAAFEAAAAeWJYWVoAAAHAAAAAFGJUUkMAAAHUAAAIDGRtZGQAAAngAAAAiGdYWVoAAApoAAAAFGdUUkMAAAHUAAAIDGx1bWkAAAp8AAAAFG1lYXMAAAqQAAAAJGJrcHQAAAq0AAAAFHJYWVoAAArIAAAAFHJUUkMAAAHUAAAIDHRlY2gAAArcAAAADHZ1ZWQAAAroAAAAh3d0cHQAAAtwAAAAFGNwcnQAAAuEAAAAN2NoYWQAAAu8AAAALGRlc2MAAAAAAAAAH3NSR0IgSUVDNjE5NjYtMi0xIGJsYWNrIHNjYWxlZAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABYWVogAAAAAAAAJKAAAA+EAAC2z2N1cnYAAAAAAAAEAAAAAAUACgAPABQAGQAeACMAKAAtADIANwA7AEAARQBKAE8AVABZAF4AYwBoAG0AcgB3AHwAgQCGAIsAkACVAJoAnwCkAKkArgCyALcAvADBAMYAywDQANUA2wDgAOUA6wDwAPYA+wEBAQcBDQETARkBHwElASsBMgE4AT4BRQFMAVIBWQFgAWcBbgF1AXwBgwGLAZIBmgGhAakBsQG5AcEByQHRAdkB4QHpAfIB+gIDAgwCFAIdAiYCLwI4AkECSwJUAl0CZwJxAnoChAKOApgCogKsArYCwQLLAtUC4ALrAvUDAAMLAxYDIQMtAzgDQwNPA1oDZgNyA34DigOWA6IDrgO6A8cD0wPgA+wD+QQGBBMEIAQtBDsESARVBGMEcQR+BIwEmgSoBLYExATTBOEE8AT+BQ0FHAUrBToFSQVYBWcFdwWGBZYFpgW1BcUF1QXlBfYGBgYWBicGNwZIBlkGagZ7BowGnQavBsAG0QbjBvUHBwcZBysHPQdPB2EHdAeGB5kHrAe/B9IH5Qf4CAsIHwgyCEYIWghuCIIIlgiqCL4I0gjnCPsJEAklCToJTwlkCXkJjwmkCboJzwnlCfsKEQonCj0KVApqCoEKmAquCsUK3ArzCwsLIgs5C1ELaQuAC5gLsAvIC+EL+QwSDCoMQwxcDHUMjgynDMAM2QzzDQ0NJg1ADVoNdA2ODakNww3eDfgOEw4uDkkOZA5/DpsOtg7SDu4PCQ8lD0EPXg96D5YPsw/PD+wQCRAmEEMQYRB+EJsQuRDXEPURExExEU8RbRGMEaoRyRHoEgcSJhJFEmQShBKjEsMS4xMDEyMTQxNjE4MTpBPFE+UUBhQnFEkUahSLFK0UzhTwFRIVNBVWFXgVmxW9FeAWAxYmFkkWbBaPFrIW1hb6Fx0XQRdlF4kXrhfSF/cYGxhAGGUYihivGNUY+hkgGUUZaxmRGbcZ3RoEGioaURp3Gp4axRrsGxQbOxtjG4obshvaHAIcKhxSHHscoxzMHPUdHh1HHXAdmR3DHeweFh5AHmoelB6+HukfEx8+H2kflB+/H+ogFSBBIGwgmCDEIPAhHCFIIXUhoSHOIfsiJyJVIoIiryLdIwojOCNmI5QjwiPwJB8kTSR8JKsk2iUJJTglaCWXJccl9yYnJlcmhya3JugnGCdJJ3onqyfcKA0oPyhxKKIo1CkGKTgpaymdKdAqAio1KmgqmyrPKwIrNitpK50r0SwFLDksbiyiLNctDC1BLXYtqy3hLhYuTC6CLrcu7i8kL1ovkS/HL/4wNTBsMKQw2zESMUoxgjG6MfIyKjJjMpsy1DMNM0YzfzO4M/E0KzRlNJ402DUTNU01hzXCNf02NzZyNq426TckN2A3nDfXOBQ4UDiMOMg5BTlCOX85vDn5OjY6dDqyOu87LTtrO6o76DwnPGU8pDzjPSI9YT2hPeA+ID5gPqA+4D8hP2E/oj/iQCNAZECmQOdBKUFqQaxB7kIwQnJCtUL3QzpDfUPARANER0SKRM5FEkVVRZpF3kYiRmdGq0bwRzVHe0fASAVIS0iRSNdJHUljSalJ8Eo3Sn1KxEsMS1NLmkviTCpMcky6TQJNSk2TTdxOJU5uTrdPAE9JT5NP3VAnUHFQu1EGUVBRm1HmUjFSfFLHUxNTX1OqU/ZUQlSPVNtVKFV1VcJWD1ZcVqlW91dEV5JX4FgvWH1Yy1kaWWlZuFoHWlZaplr1W0VblVvlXDVchlzWXSddeF3JXhpebF69Xw9fYV+zYAVgV2CqYPxhT2GiYfViSWKcYvBjQ2OXY+tkQGSUZOllPWWSZedmPWaSZuhnPWeTZ+loP2iWaOxpQ2maafFqSGqfavdrT2una/9sV2yvbQhtYG25bhJua27Ebx5veG/RcCtwhnDgcTpxlXHwcktypnMBc11zuHQUdHB0zHUodYV14XY+dpt2+HdWd7N4EXhueMx5KnmJeed6RnqlewR7Y3vCfCF8gXzhfUF9oX4BfmJ+wn8jf4R/5YBHgKiBCoFrgc2CMIKSgvSDV4O6hB2EgITjhUeFq4YOhnKG14c7h5+IBIhpiM6JM4mZif6KZIrKizCLlov8jGOMyo0xjZiN/45mjs6PNo+ekAaQbpDWkT+RqJIRknqS45NNk7aUIJSKlPSVX5XJljSWn5cKl3WX4JhMmLiZJJmQmfyaaJrVm0Kbr5wcnImc951kndKeQJ6unx2fi5/6oGmg2KFHobaiJqKWowajdqPmpFakx6U4pammGqaLpv2nbqfgqFKoxKk3qamqHKqPqwKrdavprFys0K1ErbiuLa6hrxavi7AAsHWw6rFgsdayS7LCszizrrQltJy1E7WKtgG2ebbwt2i34LhZuNG5SrnCuju6tbsuu6e8IbybvRW9j74KvoS+/796v/XAcMDswWfB48JfwtvDWMPUxFHEzsVLxcjGRsbDx0HHv8g9yLzJOsm5yjjKt8s2y7bMNcy1zTXNtc42zrbPN8+40DnQutE80b7SP9LB00TTxtRJ1MvVTtXR1lXW2Ndc1+DYZNjo2WzZ8dp22vvbgNwF3IrdEN2W3hzeot8p36/gNuC94UThzOJT4tvjY+Pr5HPk/OWE5g3mlucf56noMui86Ubp0Opb6uXrcOv77IbtEe2c7ijutO9A78zwWPDl8XLx//KM8xnzp/Q09ML1UPXe9m32+/eK+Bn4qPk4+cf6V/rn+3f8B/yY/Sn9uv5L/tz/bf//ZGVzYwAAAAAAAAAuSUVDIDYxOTY2LTItMSBEZWZhdWx0IFJHQiBDb2xvdXIgU3BhY2UgLSBzUkdCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFhZWiAAAAAAAABimQAAt4UAABjaWFlaIAAAAAAAAAAAAFAAAAAAAABtZWFzAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJYWVogAAAAAAAAAxYAAAMzAAACpFhZWiAAAAAAAABvogAAOPUAAAOQc2lnIAAAAABDUlQgZGVzYwAAAAAAAAAtUmVmZXJlbmNlIFZpZXdpbmcgQ29uZGl0aW9uIGluIElFQyA2MTk2Ni0yLTEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFhZWiAAAAAAAAD21gABAAAAANMtdGV4dAAAAABDb3B5cmlnaHQgSW50ZXJuYXRpb25hbCBDb2xvciBDb25zb3J0aXVtLCAyMDA5AABzZjMyAAAAAAABDEQAAAXf///zJgAAB5QAAP2P///7of///aIAAAPbAADAdf/bAIQAAwICCAgICAgICAcICAgFCAgICAgHBwcHBggGBwUICAgICAYICAYIBggHCAYICggHCAgJCQkGCAsNCggNCAgJCAEDBAQCAgIJAgIJCAICAggICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgI/8AAEQgBLAEsAwERAAIRAQMRAf/EABwAAQACAwEBAQAAAAAAAAAAAAAGBwMEBQgCCf/EAEcQAAICAQIDAwUIEQQCAwAAAAACAQMEERIFBhMHISIUMTJBURUXQlJiZKTjIzM0RGFxcnSBgoORkqOzxNNDU1SyoaIkscP/xAAUAQEAAAAAAAAAAAAAAAAAAAAA/8QAFBEBAAAAAAAAAAAAAAAAAAAAAP/aAAwDAQACEQMRAD8A/VMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAArntP7QWxtKqZ0tld27RWhY/EysBXHvtcQ/wCR/Jo/xgPfa4h/yP5NH+MB77XEP+R/Jo/xgPfa4h/yP5NH+MB77XEP+R/Jo/xgdDgfbDlLZE3WdSvd3xCVJ/7LXAF749+5Vb4yxMfpjUDMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAYLMtF7pZYn2S0QB8e6Nfx6/41Ae6Nfx6/41AovtnuWcuJiYmOgnmnX44ECAAAAAAvnA9U8N4gnTr8df2pfhr8WANj3Rr+PX/GoD3Rr+PX/GoGwra+YD6AAAAAAAAAAAAAAAAAAAAAAAAAACIdpHN3klMSv2yzWE/V2a//AGB5/wCIcWstaXsd2afXLAavUn2yA6k+2QPlm18/eAAAAAAAB9dSfbIDqT7ZAdZvbP8AEBZXZf2gWxctFrSyO0ImvwZZtIAu8AAAAAAAAAAAAAAAAAAAAAAAAAAKh7ffvT9v/agVEAAAANrh/C7Lm21rLT7IAsLgvYpc+k3NCRPfpHfIEhXsKo/3rf3KBp5/YUkRPTteZ9UNtAgnMXZ/kY3ey6r62TvgCMgAO9yLw+q3JrS6YhJ8+vmb5IE/7WeWcSqiGqWuuzqREQvwobzgV1yh914v59R/VQD1GAAAAAAAAAAAAAAAAAAAAAAAAAAFQ9vv3p+3/tQKiAAAOxyryy+VbFaR3edp9SwBeePi4vC6NZ0idvnn07J9gFccxdsl9kzFMRWnqn4YEVs5yypmZm+3v9jsB0uFdpuXVp9kl9PU/eBafKHaVTlxFVkQtkrpMN6Dz7IAi/aX2YwizfRGkR3unxY+SBVAH0rTGkx3THmkDNlcQezTe0tt7o3NrtA6HKH3Xi/n1H9VAPUYAAAAAAAAAAAAAAAAAAAAAAAAAAVD2+/en7f+1AqIAAA9A8gcHXDxJts0iWXqO3r09UAU5zlzU+VazNPghtEj1LEAcEAAA+qbpWYlZmJjviY86geg+zbmryuiVs0mxPC0fGXzQwFRdonLfk2QyrrsnxRP5XfoBFwAHY5Q+68X8+o/qoB6jAAAAAAAAAAAAAAAAAAAAAAAAAACoe3370/b/wBqBUQADqcs4sPkUpPma2In+IC5+1ziHRxIrWdIsbp6fg2gUIAAAAAE47IeKTXlwkea3wyBLe3XC1Sl4jvVpiZ/ABTQADscofdeL+fUf1UA9RgAAAAAAAAAAAAAAAAAAAAAAAAABUPb796ft/7UCogAHY5NbTKx9f8AfT/sBbPbjTrj1zHmW/Wf4QKOAAAAACVdmOPLZtEx5lfdIFj9tuVpQi6ek89/5IFHAAOxyh914v59R/VQD1GAAAAAAAAAAAAAAAAAAAAAAAAAAFQ9vv3p+3/tQKiAAZsPKlGho86trH6oHofNx4zsDSNJZ6PDPxWA87ZWPKsytGkq0xMfkgYwAAABbnYly5Mb8ho8MrtT8at5wOb218dh7lpidYrXWfxsBWoADscofdeL+fUf1UA9RgAAAAAAAAAAAAAAAAAAAAAAAAABUPb796ft/wC1AqIAAAsfsm55iluha2lbz4WnzK34QJJ2kdmnX+z0ab9NWX1PHtj5QFNZmC9bSrrKtHnie6QMIACbcmdmduS0M6zXVDd8z3S0fJAtXmfmKrh+PCppDQmlafh9sgees7Maxpd51lmmZn8oDCAA7HKH3Xi/n1H9VAPUYAAAAAAAAAAAAAAAAAAAAAAAAAAVD2+/en7f+1AqIAAAAWByZ2r2Y8RXbE2Vx5vjr+kCxV4rw/MWJaapaV74n7YoGD3r8Ce/We/5YGxjcq8PxvE3T/HZOoHK5j7X6KllMeIsaO5Wj7WoFOcY41Ze82WTq0/uX8QGiAAAdjlD7rxfz6j+qgHqMAAAAAAAAAAAAAAAAAAAAAAAAAAKp7d+HsyUPEarX1d0/F39DT/qBTQAAAAAfS2THfEzE+2AM3ujZ/uWfxsB82ZjN3SzzH4WaQMIAAAAASDkTBZ8vH2xrty6nb8SOkgemwAAAAAAAAAAAAAAAAAAAAAAAAAA1OI8NS1JreNytGkxIFaZ/YQjNMpkdNZ8y9Hft/T1FA1/eC+d/R/rgHvBfO/o/wBcA94L539H+uAe8F87+j/XAPeC+d/R/rgHvBfO/o/1wD3gvnf0f64B7wXzv6P9cA94L539H+uAe8F87+j/AFwD3gvnf0f64B7wXzv6P9cBNeT+RKsSJ2+J57peV0n926QJSAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFVcoc5PTw2y+xrL7fLraqVd2d7bXs2U1Qzd/s/8AIGr2e86WUY185XlWVdHGbaIWpWyLGmuqlphFZo0rjSwCX8L7TsaxL2eLsecdVa6vJqaq2tX12Ns8WsN8nUDDwbtVousStqsqibftL5FPSryPZ033Nu1ArzN4/TlW5NmWnEr6ast6q6cVH8nqpq7t90qyfbdO/wAYE5p55wMLHw4qW3ye9bIx+mrWbdne0N1Gm7dvbb8PxAdLgXaNTe1lfSyqbq6upNF9PTvdI+Eibp3d4GridqtU2V1242fjdWzYj5ON0a2efMm7dPfIGT3z6Olk2PXk1zi6TdTZVCX7XbRGVGbayP7dwHQnnWuchMZUud3qWxmSvWvGR1eU6z7vBL6d0AcjJ7XsZWbSrLelH2PlV48viVyrbW1thvg/JWQO+nNVc5KYsQ0u+H5Qrxtmua9+zz7t27X8AGjxPtBpp8s3JbPkK0tbtVPF5TG5OnusjX8O7YBm45zxTjvjVvumzKtWupViJmN7LG59WjbXrMd/eBz+L9qFNNj1rTl5E1Tpc+NR1q8edJmYsfcu2YgCU8L4kl1aW1zDV2JDK0euJA2wAAAAAAAAAAAAAAAAAAAAAAAABSXYzwl73l7I0ow8u+ao9VmRc3e/7Or+qB98scyU0U8QW976Uu47mL16FdpomFxu/fWrsjNr4fABwfctr6s7yTyjMohsW2L8hbGyMmaH8dMPYsNYqr36bAJXzHzdTxNsOjE6j2JxKi+2ZqdPJa6t2/ezLt3d/wACQHLvN1XC2ysXKh1ac+67H21u/lSXbJhU2Lt6mvqAj9NU4K8HbJia4XMzLnTazTQtqrMLtXc3hidZ9gHaz+Pvl5VmZgI1i4vCrkW1qnVbrn71RFsVWfZ5wIpm8US5cVvLs/JuniGL1q3R68Sid6bolGrivqbvR2OB2O01my/K8jHnZRj4vk910RujObqo3RX4PTpb/W79JkCQcmVtj234mQ+t+bV1acue6MmFq2dLb8CyiPMkfAAhXCqMWqnybMzOMY96q1b4iO/SshmddKUWl65psj4zASjJ5YW3Pw6VuzcdU5eTa1dsUZWiWwqpbKr/ABR8cDlcY4T0K+PVdS+7bVgfZL36lrbk18T7V82oHR4nwuyYwMy+NLsjj2FCp5/JsdOt0qf/ANH+XIHGpopxbMirNzeKYTzmW2V9Cy2MfJrdt0OnTps8XxgLa7PMOuvDpWnyjpQrynlCxF2jWu3jhVX2+Hu9HQCTAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEd49yt178S/ft8ltsfbs3dXqoq6a7o2afpAkQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADQp4vU1c2rZU1Sq0zYrq1awnpaurbfDp3+z94Hzk8coSuLXuqSptJW1rEStofvjR2nbOv/kDYws5LFh63WxJ8zIyurfilZ2gYeI8bppiJutqqifNNtiVw34uo0AfLceoiuLpvpimf9WbU6c/r7tgHxhcxY9is9d9NiJ6bpajpX3a+KVbbHhA1aud8Jp0XLxGnSZ0jJpmdIjWfRb1QBsJzTjSyJGRRLusTWsXVS9kT5pRd2rfoA2sPiNdm6EdHmt9jwrq0o0fBfb6LfgkDXo5kx3fprfQ1kzMQi3Vs+qelG1W3ar6wMnu3TpY3Vq0qnS1uomlMx6rJ3eCfygPnI4/QtcXNdStTabbGtRa23ebR2nb3gfXu5T0+t1qujpr1eqnR0109Pd09P0gZcniNabdzou94RNzqu9n9FV3ek8+pYA1uIcxY9TQlt9NbN6K2WojN6u6GaJA6gAAAAAAAAAAAAAAAAAAAAAAAAAAUJy97o+5V/S8g8l6OZr1PKfKNu6/qabfsO70toHzGHffkYVSrg2QvAMVqa8/qtU29EW2a0r8L3erxAT7s05eux3y1tnDiHsrsWjEZ+nQ0q8P9jtXWvqaLP6AOlznwJb9NleDdlVprWmZE2Vqjsu+emurd+yNHhfPAFZcD4slFV+M2Lj5GRZxvp9CdnkFdjr4Gr6iztrVY9m8AuDbTl5C2V4NLPy/lS1eErIixprHWVv9X8kCUcv8s40cIi6MfHi33IsbqxSnU1mh+/ft6m4CNZvLmGnA0u6dS3zj1ulsbeu125O5X+2fqagdfjPGn4bfZdMT/wDO4XDRG37/AMdEVYmPl7+8DLzHylVi8NoloiMylq2pthIe+zLd+p01lfG8PZLL6XmAiGBxCyy62vMqijFt49D5Uyy2Ity1pKYz6d3Rd477AJHzZwu2zia0VV8OlK+HL5PRmq/k+jM6v0qqvBNnq9H0ANPjPL12Pw/iq2zhx1PJ7Fow5fp0NLor/Y7V1r6ui/uA6PMXujrw3yryDo+7OHt8n8o62/x6a9b7Ht03AZ+SeCYuQufZmV1WXxxC9bmuhZeiuvuTbu+1oq+aUAkPYxkO3DqJaZnRrFRm87Vpa6oBOgAAAAAAAAAAAAAAAAAAAAAAAABx8PlWiuhsZK9tDLYrJvedYu3b/EzS/i3T8L1gavFOQMS+qum2hXrqrVK9WeHrVIWIiLFbq+qPh/vA2OXOUcfEWVx6orhp1bSWaW082rWMzT+8DX5k5DxMyVnIpWxljSJ3OjRHs3VsraAYve5wvJ/JfJ06G7ds8Wu7zb9+7q9T5e7UDFw7sywadZrx1SZx3paYe3Vq7fTWfF37vj+l+EDf4hwOEw7MahNIjCsqqTX21uqLrY3/AGkCL8kdk+LVXj2W4qxlJUkvMuzwti+vatjUbvwwB0+aeX7MjKw4muJx6LGyLLJlPFakaU1qu7qel428OwDvZfL1Nltd7pusp16UzL7a9/nmE3dPqfL27gNZ+T8aVvSaolMmyXvWWeYsZvhd7eHzfA0AxcV5DxL60qupixK1iElnsmxIX1dXd1v/AHAxYvZvhJRZjrRC026dRYezWzZOsbn3dXu/LA6vEeA1XRVFi7ujel1XiaNllXoN4ZjXbr69QORxzs0wcm3rXY6PZ3atEum7T48Vsqv+vEgSHHxlRYVYhVWIiFWNIWI80REAbAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH//Z";

    @Autowired
    public PosterService(PosterRepo posterRepo, MetadataRepo metadataRepo) {
        this.posterRepo = posterRepo;
        this.metadataRepo = metadataRepo;
    }

    public String save(MultipartFile file) throws IOException, NullPointerException, IllegalArgumentException {
        Poster poster = new Poster();
        int hash = Objects.requireNonNull(file.getOriginalFilename()).hashCode();
        if (hash == 0) {
            throw new IllegalArgumentException();
        }
        poster.setImage(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
        poster = posterRepo.insert(poster);
        return poster.getId();
    }

    public PosterWithMetadata getPosterWithMetadataById(final String id) {
        Poster poster = posterRepo.getPosterById(id);
        if (poster == null) {
            throw new NullPointerException();
        }
        return new PosterWithMetadata(
                metadataRepo.getByPosterId(poster.getId()).getId(),
                poster.getImage().getData());
    }

    public boolean deleteById(String id) {
        return posterRepo.deletePosterById(id) > 0;
    }

    public List<String> getRecentSavedPosterIds(int amount) {
        Pageable requestedAmountRestriction = PageRequest.of(0, amount);
        return metadataRepo.findTopNByOrderByCreatedAtDesc(requestedAmountRestriction).stream()
                .map(ContentMetadata::getPosterId)
                .toList();
    }

    public List<Map<String, byte[]>> getRecentlySavedPosters(int amount) {
        List<String> recentSavedPosterIds = getRecentSavedPosterIds(amount);
        List<Map<String, byte[]>> imagesAndMetadata = new ArrayList<>(amount);

        for (String id : recentSavedPosterIds) {
            Map<String, byte[]> data = new HashMap<>();

            Poster poster = posterRepo.getPosterById(id);
            if (poster == null) {
                data.put("metadataId", "metadataId: NULL".getBytes());
                data.put("title", "<Название>".getBytes());
                data.put("country", "<Страна>".getBytes());
                data.put("releaseDate", "<Дата релиза>".getBytes());
                data.put("mainGenre", "<Основной жанр>".getBytes());
                data.put("subGenres", "<Дополнительные жанры>".getBytes());
                data.put("age", "0".getBytes());
                data.put("rating", "0.00".getBytes());
                data.put("poster", defaultNotFoundImage.getBytes());
                data.put("videoId", "videoId: NULL".getBytes());
            } else {
                final ContentMetadata metadata = metadataRepo.getByPosterId(poster.getId());

                data.put("metadataId", metadata.getId().getBytes());
                data.put("title", metadata.getTitle().getBytes());
                data.put("releaseDate", metadata.getReleaseDate().getBytes());
                data.put("country", metadata.getCountry().getBytes());
                data.put("mainGenre", metadata.getMainGenre().getBytes());
                data.put("subGenres", metadata.getSubGenres().toString().replace("[", "").replace("]", "").getBytes());
                data.put("age", metadata.getAge().toString().getBytes());
                data.put("rating", String.valueOf(metadata.getRating()).getBytes());
                data.put("poster", poster.getImage().getData());
                data.put("videoId", metadata.getVideoId().getBytes());
            }

            imagesAndMetadata.add(data);
        }
        return imagesAndMetadata;
    }

    public ResponseEntity<List<ContentMetadata>> getMetadataByTitle(String title) {
        List<ContentMetadata> occurrences = metadataRepo.getByTitle(title);
        if (occurrences == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(occurrences);
    }

    public ResponseEntity<ContentMetadata> getMetadataById(String id) {
        ContentMetadata metadata = metadataRepo.getById(id);
        if (metadata == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(metadata);
    }

}
