package org.hotel.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormDataWithFiles {

    private List<CommonsMultipartFile> file;
    private String title;
    private String category;
    private String house_id;
    private String room_id;
}